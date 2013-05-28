package actors

import akka.actor.{Props, Actor}
import utils.{StockQuote, Global, FakeStockQuote}
import java.util.Random
import scala.collection.immutable.Queue

class StockActor(symbol: String) extends Actor {

  lazy val stockQuote : StockQuote = new FakeStockQuote

  // A random data set which uses stockQuote.newPrice to get each data point
  var stockHistory: Queue[java.lang.Double] = {
    lazy val initialPrices: Stream[java.lang.Double] = (new Random().nextDouble * 800) #:: initialPrices.map(previous => stockQuote.newPrice(previous))
    initialPrices.take(50).to[Queue]
  }

  def receive = {
    case FetchLatest =>
      val newPrice = stockQuote.newPrice(stockHistory.last.doubleValue())
      stockHistory = stockHistory.drop(1) :+ newPrice
      Global.usersActor ! StockUpdate(symbol, newPrice)
    case FetchHistory =>
      sender ! stockHistory
  }
}

class StockHolderActor extends Actor {
  def receive = {
    case SetupStock(symbol) =>
      sender ! context.child(symbol).getOrElse(context.actorOf(Props(new StockActor(symbol)), symbol))
    case FetchLatest =>
      for(child <- context.children) child ! FetchLatest
  }
}

case object FetchLatest {
  def instance = this
}

case class SetupStock(symbol: String)

case class StockUpdate(symbol: String, price: Number)

case object FetchHistory
