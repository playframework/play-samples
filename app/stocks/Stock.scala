package stocks

import akka.NotUsed
import akka.stream.{Materializer, ThrottleMode}
import akka.stream.scaladsl.{Sink, Source}

import scala.concurrent.duration._

/**
 * A stock is a source of stock quotes and a symbol.
 */
class Stock(val symbol: StockSymbol) {
  private val stockQuoteGenerator: StockQuoteGenerator = new FakeStockQuoteGenerator(symbol)

  private val source: Source[StockQuote, NotUsed] = {
    Source.unfold(stockQuoteGenerator.seed) { (last: StockQuote) =>
      val next = stockQuoteGenerator.newQuote(last)
      Some(next, next)
    }
  }

  /**
   * Returns N elements of the stockquote source.
   */
  def history(n: Int)(implicit mat: Materializer): Source[Seq[StockQuote], NotUsed] = {
    // It's possible to do far more advanced windows with Akka Streams, i.e.
    // https://softwaremill.com/windowing-data-in-akka-streams/
    val stockHistory = source.take(n).runWith(Sink.seq)
    Source.fromFuture(stockHistory)
  }

  /**
   * Provides a source that returns a stock quote every 75 milliseconds.
   */
  def update: Source[StockQuote, NotUsed] = {
    source.throttle(elements = 1, per = 75.millis, maximumBurst = 1, ThrottleMode.shaping)
  }

  override val toString: String = s"Stock($symbol)"
}

trait StockQuoteGenerator {
  def seed: StockQuote
  def newQuote(lastQuote: StockQuote): StockQuote
}

class FakeStockQuoteGenerator(symbol: StockSymbol) extends StockQuoteGenerator {
  private def random: Double = scala.util.Random.nextDouble

  def seed: StockQuote = {
    StockQuote(symbol, StockPrice(random * 800))
  }

  def newQuote(lastQuote: StockQuote): StockQuote = {
    StockQuote(symbol, StockPrice(lastQuote.price.raw * (0.95 + (0.1 * random))))
  }
}

case class StockQuote(symbol: StockSymbol, price: StockPrice)

/** Value class for a stock symbol */
class StockSymbol private (val raw: String) extends AnyVal {
  override def toString: String = raw
}

object StockSymbol {
  import play.api.libs.json._ // Combinator syntax

  def apply(raw: String) = new StockSymbol(raw)

  implicit val stockSymbolReads: Reads[StockSymbol] = {
    JsPath.read[String].map(StockSymbol(_))
  }

  implicit val stockSymbolWrites: Writes[StockSymbol] = Writes {
    (symbol: StockSymbol) => JsString(symbol.raw)
  }
}

/** Value class for stock price */
class StockPrice private (val raw: Double) extends AnyVal {
  override def toString: String = raw.toString
}

object StockPrice {
  import play.api.libs.json._ // Combinator syntax

  def apply(raw: Double):StockPrice = new StockPrice(raw)

  implicit val stockPriceWrites: Writes[StockPrice] = Writes {
    (price: StockPrice) => JsNumber(price.raw)
  }
}

// Used for automatic JSON conversion
// https://www.playframework.com/documentation/2.6.x/ScalaJson

// JSON presentation class for stock history
case class StockHistory(symbol: StockSymbol, prices: Seq[StockPrice])

object StockHistory {
  import play.api.libs.json._ // Combinator syntax

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
  import play.api.libs.json._ // Combinator syntax

  implicit val stockUpdateWrites: Writes[StockUpdate] = new Writes[StockUpdate] {
    override def writes(update: StockUpdate): JsValue = Json.obj(
      "type" -> "stockupdate",
      "symbol" -> update.symbol,
      "price" -> update.price
    )
  }
}
