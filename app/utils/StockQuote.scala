package utils

trait StockQuote {
  def newPrice(lastPrice: Double): Double
}
