package actors

import stocks._

object Messages {

  case class Stocks(stocks: Set[Stock]) {
    require(stocks.nonEmpty, "Must specify at least one stock!")
  }

  case class WatchStocks(symbols: Set[StockSymbol]) {
    require(symbols.nonEmpty, "Must specify at least one symbol!")
  }

  case class UnwatchStocks(symbols: Set[StockSymbol]) {
    require(symbols.nonEmpty, "Must specify at least one symbol!")
  }
}


