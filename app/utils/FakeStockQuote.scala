package utils

class FakeStockQuote extends StockQuote {
  private def random: Double = scala.util.Random.nextDouble

  def newPrice(lastPrice: Double): Double = {
    lastPrice * (0.95 + (0.1 * random))
  }
}
