package actors

import akka.actor.typed.{ ActorRef, Behavior }
import akka.actor.typed.scaladsl.Behaviors
import stocks._

import scala.collection.mutable

/**
 * This actor contains a set of stocks internally that may be used by
 * all websocket clients.
 */
object StocksActor {
  final case class Stocks(stocks: Set[Stock]) {
    require(stocks.nonEmpty, "Must specify at least one stock!")
  }

  final case class GetStocks(symbols: Set[StockSymbol], replyTo: ActorRef[Stocks])

  def apply(
      stocksMap: mutable.Map[StockSymbol, Stock] = mutable.HashMap(),
  ): Behavior[GetStocks] = {
    // May want to remove stocks that aren't viewed by any clients...
    Behaviors.logMessages(
      Behaviors.receiveMessage {
        case GetStocks(symbols, replyTo) =>
          val stocks = symbols.map(symbol => stocksMap.getOrElseUpdate(symbol, new Stock(symbol)))
          replyTo ! Stocks(stocks)
          Behaviors.same
      }
    )
  }
}
