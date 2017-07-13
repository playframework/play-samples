package actors

import javax.inject._

import akka.actor._
import akka.event.{LogMarker, MarkerLoggingAdapter}
import akka.stream._
import akka.stream.scaladsl._
import akka.util.Timeout
import akka.{Done, NotUsed}
import com.google.inject.assistedinject.Assisted
import play.api.libs.json._
import stocks._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

/**
 * Creates a user actor that sets up the websocket stream.  Although it's not required,
 * having an actor manage the stream helps with lifecycle and monitoring, and also helps
 * with dependency injection through the AkkaGuiceSupport trait.
 *
 * @param stocksActor the actor responsible for stocks and their streams
 * @param ec          implicit CPU bound execution context.
 */
class UserActor @Inject()(@Assisted id: String, @Named("stocksActor") stocksActor: ActorRef)
                         (implicit mat: Materializer, ec: ExecutionContext)
  extends Actor {
  import Messages._

  // Useful way to mark out individual actors with websocket request context information...
  private val marker = LogMarker(name = self.path.name)
  implicit val log: MarkerLoggingAdapter = akka.event.Logging.withMarker(context.system, this.getClass)

  implicit val timeout = Timeout(50.millis)

  val (hubSink, hubSource) = MergeHub.source[JsValue](perProducerBufferSize = 16)
    .toMat(BroadcastHub.sink(bufferSize = 256))(Keep.both)
    .run()

  private var stocksMap: Map[StockSymbol, UniqueKillSwitch] = Map.empty

  private val jsonSink: Sink[JsValue, Future[Done]] = Sink.foreach { json =>
    // When the user types in a stock in the upper right corner, this is triggered,
    val symbol = (json \ "symbol").as[StockSymbol]
    addStocks(Set(symbol))
  }

  // If this actor is killed directly, stop anything that we started running explicitly.
  override def postStop(): Unit = {
    log.info(marker, s"Stopping actor $self")
    unwatchStocks(stocksMap.keySet)
  }

  /**
   * The receive block, useful if other actors want to manipulate the flow.
   */
  override def receive: Receive = {
    case WatchStocks(symbols) =>
      addStocks(symbols)
      sender() ! websocketFlow

    case UnwatchStocks(symbols) =>
      unwatchStocks(symbols)
  }

  /**
   * Generates a flow that can be used by the websocket.
   *
   * @return the flow of JSON
   */
  private lazy val websocketFlow: Flow[JsValue, JsValue, NotUsed] = {
    // Put the source and sink together to make a flow of hub source as output (aggregating all
    // stocks as JSON to the browser) and the actor as the sink (receiving any JSON messages
    // from the browse), using a coupled sink and source.
    Flow.fromSinkAndSourceCoupled(jsonSink, hubSource).watchTermination() { (_, termination) =>
      // When the flow shuts down, make sure this actor also stops.
      termination.foreach(_ => context.stop(self))
      NotUsed
    }
  }

  /**
   * Adds several stocks to the hub, by asking the stocks actor for stocks.
   */
  private def addStocks(symbols: Set[StockSymbol]): Future[Unit] = {
    import akka.pattern.ask

    // Ask the stocksActor for a stream containing these stocks.
    val future = (stocksActor ? WatchStocks(symbols)).mapTo[Stocks]

    // when we get the response back, we want to turn that into a flow by creating a single
    // source and a single sink, so we merge all of the stock sources together into one by
    // pointing them to the hubSink, so we can add them dynamically even after the flow
    // has started.
    future.map { (newStocks: Stocks) =>
      newStocks.stocks.foreach { stock =>
        if (! stocksMap.contains(stock.symbol)) {
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
    // We convert everything to JsValue so we get a single stream for the websocket.
    // Make sure the history gets written out before the updates for this stock...
    val historySource = stock.history(50).map(sh => Json.toJson(sh))
    val updateSource = stock.update.map(su => Json.toJson(su))
    val stockSource = historySource.concat(updateSource)

    // Set up a flow that will let us pull out a killswitch for this specific stock,
    // and automatic cleanup for very slow subscribers (where the browser has crashed, etc).
    val killswitchFlow: Flow[JsValue, JsValue, UniqueKillSwitch] = {
      Flow.apply[JsValue]
        .joinMat(KillSwitches.singleBidi[JsValue, JsValue])(Keep.right)
        .backpressureTimeout(1.seconds)
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
    stocksMap += (stock.symbol -> killSwitch)
  }

  def unwatchStocks(symbols: Set[StockSymbol]): Unit = {
    symbols.foreach { symbol =>
      stocksMap.get(symbol).foreach { killSwitch =>
        killSwitch.shutdown()
      }
      stocksMap -= symbol
    }
  }
}


object UserActor {
  trait Factory {
    def apply(id: String): Actor
  }
}
