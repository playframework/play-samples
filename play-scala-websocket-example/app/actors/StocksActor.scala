package actors

import akka.actor.{Actor, ActorLogging}
import akka.event.LoggingReceive
import stocks._

import scala.collection.mutable

/**
 * This actor contains a set of stocks internally that may be used by
 * all websocket clients.
 */
class StocksActor extends Actor with ActorLogging {
  import Messages._

  // May want to remove stocks that aren't viewed by any clients...
  private val stocksMap: mutable.Map[StockSymbol, Stock] = mutable.HashMap()

  def receive = LoggingReceive {
    case WatchStocks(symbols) =>
      val stocks = symbols.map(symbol => stocksMap.getOrElseUpdate(symbol, new Stock(symbol)))
      sender ! Stocks(stocks)
  }
}
