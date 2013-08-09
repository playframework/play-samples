package actors

import akka.actor.{Props, ActorRef, Actor}
import utils.{StockQuote, FakeStockQuote}
import java.util.Random
import scala.collection.immutable.Queue
import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import play.libs.Akka

/**
 * There is one StockActor per stock symbol.  The StockActor maintains a list of users watching the stock and the stock
 * values.  Each StockActor updates a rolling dataset of randomly generated stock values.
 */

class StockActor(symbol: String) extends Actor {

  lazy val stockQuote: StockQuote = new FakeStockQuote
  
  var watchers: Vector[String] = Vector.empty

  // A random data set which uses stockQuote.newPrice to get each data point
  var stockHistory: Queue[java.lang.Double] = {
    lazy val initialPrices: Stream[java.lang.Double] = (new Random().nextDouble * 800) #:: initialPrices.map(previous => stockQuote.newPrice(previous))
    initialPrices.take(50).to[Queue]
  }
  
  // Fetch the latest stock value every 75ms
  context.system.scheduler.schedule(Duration.Zero, 75.millis, self, FetchLatest)

  def receive = {
    case FetchLatest =>
      // add a new stock price to the history and drop the oldest
      val newPrice = stockQuote.newPrice(stockHistory.last.doubleValue())
      stockHistory = stockHistory.drop(1) :+ newPrice
      // notify watchers
      watchers.foreach(uuid => context.actorSelection(s"/user/$uuid") ! StockUpdate(symbol, newPrice))
    case WatchStock(uuid) =>
      // send the stock history to the user
      context.actorSelection(s"/user/$uuid") ! StockHistory(symbol, stockHistory.asJava)
      // add the watcher to the list
      watchers = watchers :+ uuid
  }
}

class StocksActor extends Actor {
  def receive = {
    case SetupStock(uuid, symbol) =>
      context.child(symbol).getOrElse(context.actorOf(Props(new StockActor(symbol)), symbol)) ! WatchStock(uuid)
  }
}

object StocksActor {
  lazy val stocksActor: ActorRef = Akka.system.actorOf(Props(classOf[StocksActor]))
}


case object FetchLatest

case class SetupStock(uuid: String, symbol: String)

case class StockUpdate(symbol: String, price: Number)

case class StockHistory(symbol: String, history: java.util.List[java.lang.Double])

case class WatchStock(uuid: String)