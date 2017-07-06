package actors

import javax.inject._

import akka.actor._
import akka.event.{LogMarker, LoggingReceive, MarkerLoggingAdapter}
import akka.stream._
import akka.stream.scaladsl._
import akka.util.Timeout
import akka.{Done, NotUsed}
import com.google.inject.assistedinject.Assisted
import play.api.Configuration
import play.api.libs.concurrent.InjectedActorSupport
import play.api.libs.json._
import stocks._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

/**
 * Creates a user actor that manages the websocket stream.  Having an actor manage
 * the stream helps with lifecycle and monitoring, 
 *
 * @param stocksActor the actor responsible for stocks and their streams
 * @param ec          implicit CPU bound execution context.
 */
class UserActor @Inject()(@Assisted id: String, @Named("stocksActor") stocksActor: ActorRef)
                         (implicit mat: Materializer, ec: ExecutionContext)
  extends Actor with ActorLogging {

  private val marker = LogMarker(id)
  override implicit val log: MarkerLoggingAdapter = akka.event.Logging.withMarker(context.system, this)

  implicit val timeout = Timeout(50.millis)

  val (hubSink, hubSource) = MergeHub.source[JsValue](perProducerBufferSize = 16)
    .toMat(BroadcastHub.sink(bufferSize = 256))(Keep.both)
    .run()

  private var stocksMap: Map[Stock, UniqueKillSwitch] = Map.empty

  private val jsonSink: Sink[JsValue, Future[Done]] = Sink.foreach { json =>
    // When the user types in a stock in the upper right corner, this is triggered,
    val symbol = (json \ "symbol").as[StockSymbol]
    addStocks(Set(symbol))
  }

  /**
   * Receive block for this actor.  Note that this gets messages both from the
   * system and from the websocket (in the form of JsValue).
   */
  override def receive: Receive = LoggingReceive {
    case WatchStocks(symbols) =>
      val websocketFlow = generateFlow()
      addStocks(symbols)
      sender() ! websocketFlow
  }

  /**
   * Generates a flow that can be used by the websocket.
   *
   * @return the flow of JSON
   */
  private def generateFlow(): Flow[JsValue, JsValue, NotUsed] = {
    // Put the source and sink together to make a flow of hub source as output (aggregating all
    // stocks as JSON to the browser) and the actor as the sink (receiving any JSON messages
    // from the browse
    Flow.fromSinkAndSource(jsonSink, hubSource)
  }

  /**
   * Adds several stocks to the hub, by asking the stocks actor for stocks.
   */
  private def addStocks(symbols: Set[StockSymbol]): Future[Unit] = {
    import akka.pattern.ask

    // Ask the stocksActor for a stream containing these stocks.
    val future = (stocksActor ? WatchStocks(symbols)).mapTo[Stocks]

    // when we get the response back, we want to turn that into a flow by creating a single
    // source and a single sink, so we merge all of the stock sources together into one, and
    // set the actor itself up as the sink.
    future.map { (newStocks: Stocks) =>
      newStocks.stocks.foreach { stock =>
        if (! stocksMap.contains(stock)) {
          log.info(marker, s"Adding stock $stock")
          addStock(stock)
        }
      }
    }
  }

  /**
   * Adds a single stock to the hub.
   */
  private def addStock(stock: Stock): Unit = {
    // Make sure the history gets written out before the updates for this stock...
    val historySource: Source[JsValue, NotUsed] = generateStockHistorySource(stock)
      .named(s"history-${stock.symbol}-$id")
      //.log(s"history-${stock.symbol}-$id")
    val updateSource: Source[JsValue, NotUsed] = generateStockUpdateSource(stock)
      .named(s"update-${stock.symbol}-$id")
      //.log(s"update-${stock.symbol}-$id")
    val stockSource: Source[JsValue, NotUsed] = historySource.concat(updateSource)

    // Set up a flow that will let us pull out a killswitch for this specific stock.
    val killswitchFlow: Flow[JsValue, JsValue, UniqueKillSwitch] = {
      Flow.apply[JsValue].joinMat(KillSwitches.singleBidi[JsValue, JsValue])(Keep.right)
    }

    // Set up a complete runnable graph from the stock source to the hub's sink
    val graph: RunnableGraph[UniqueKillSwitch] = {
      stockSource
        .viaMat(killswitchFlow)(Keep.right)
        .to(hubSink)
        .named(s"stock-${stock.symbol}-$id")
    }

    // Start it up!
    val killSwitch = graph.run()

    // Pull out the kill switch so we can stop it when we want to unwatch a stock.
    stocksMap += (stock -> killSwitch)
  }

  /**
   * Generates the stockhistory source by taking the first 50 elements of each source.
   */
  private def generateStockHistorySource(stock: Stock): Source[JsValue, NotUsed] = {
    val stockHistory = stock.source.take(50).runWith(Sink.seq).map { s =>
      Json.toJson(StockHistory(stock.symbol, s.map(_.price)))
    }
    Source.fromFuture(stockHistory)
  }

  /**
   * Generates the stockupdate source
   *
   * @return a source containing the merged sources of all the stocks.
   */
  private def generateStockUpdateSource(stock: Stock): Source[JsValue, NotUsed] = {
    // Throttle the updates so they only happen once per 75 millis
    stock.source
      .throttle(elements = 1, per = 75.millis, maximumBurst = 1, ThrottleMode.shaping)
      .map { stockQuote =>
        Json.toJson(StockUpdate(stockQuote.symbol, stockQuote.price))
      }
  }

  // Used for automatic JSON conversion
  // https://www.playframework.com/documentation/2.6.x/ScalaJson

  // JSON presentation class for stock history
  case class StockHistory(symbol: StockSymbol, prices: Seq[StockPrice])

  object StockHistory {
    implicit val stockHistoryWrites: Writes[StockHistory] = new Writes[StockHistory] {
      override def writes(history: StockHistory): JsValue = Json.obj(
        "type" -> "stockhistory",
        "symbol" -> history.symbol,
        "history" -> history.prices
      )
    }
  }

  // JSON presentation class for stock update
  case class StockUpdate(symbol: StockSymbol, price: StockPrice)

  object StockUpdate {
    implicit val stockUpdateWrites: Writes[StockUpdate] = new Writes[StockUpdate] {
      override def writes(update: StockUpdate): JsValue = Json.obj(
        "type" -> "stockupdate",
        "symbol" -> update.symbol,
        "price" -> update.price
      )
    }
  }
}

/**
 * Provide some DI and configuration sugar for new UserActor instances.
 */
class UserParentActor @Inject()(childFactory: UserActor.Factory,
                                configuration: Configuration)
                               (implicit ec: ExecutionContext)
  extends Actor with InjectedActorSupport with ActorLogging {

  import UserParentActor._
  import akka.pattern.{ask, pipe}

  implicit val timeout = Timeout(2.seconds)

  private val defaultStocks = configuration.get[Seq[String]]("default.stocks").map(StockSymbol(_))

  override def receive: Receive = LoggingReceive {
    case Create(id) =>
      val name = s"userActor-$id"
      log.info(s"Creating user actor $name with default stocks $defaultStocks")
      val child: ActorRef = injectedChild(childFactory(id), name)
      val future = (child ? WatchStocks(defaultStocks.toSet)).mapTo[Flow[JsValue, JsValue, _]]
      pipe(future) to sender()
  }
}

object UserParentActor {
  case class Create(id: String)
}

object UserActor {
  trait Factory {
    def apply(id: String): Actor
  }
}
