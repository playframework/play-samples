package actors

import akka.actor._
import akka.testkit._

import org.specs2.mutable._
import org.specs2.time.NoTimeConversions

import scala.concurrent.duration._
import scala.collection.immutable.Queue

import utils.StockQuote
import java.util.UUID
import play.api.Logger

class StockActorSpec extends TestkitExample with Specification with NoTimeConversions {

  /*
   * Running tests in parallel (which would ordinarily be the default) will work only if no
   * shared resources are used (e.g. top-level actors with the same name or the
   * system.eventStream).
   *
   * It's usually safer to run the tests sequentially.
   */
  sequential

  final class StockActorWithStockQuote(symbol:String, price:Double, watcher: String) extends StockActor(symbol) {
    watchers = Vector(watcher)
    override lazy val stockQuote = new StockQuote {
      def newPrice(lastPrice: java.lang.Double): java.lang.Double = price
    }
  }

  "A StockActor" should {
    val symbol = "ABC"
    
    "notify watchers when a new stock is received" in {
      // Create a stock actor with a stubbed out stockquote price and watcher
      val probe = new TestProbe(system)
      val price = 1234.0
      val uuid = UUID.randomUUID().toString
      val stockActor = system.actorOf(Props(new StockActorWithStockQuote(symbol, price, uuid)))
      
      system.actorOf(Props(new ProbeWrapper(probe)), uuid)

      // Fire off the message...
      stockActor ! FetchLatest

      // ... and ask the probe if it got the StockUpdate message.
      val actualMessage = probe.receiveOne(500 millis)
      val expectedMessage = StockUpdate(symbol, price)
      actualMessage must === (expectedMessage)
    }
    "add a watcher and send a StockHistory message to the user when receiving WatchStock message" in {
      val probe = new TestProbe(system)
      
      // Create a standard StockActor.
      val stockActor = system.actorOf(Props(new StockActor(symbol)))

      val uuid = UUID.randomUUID().toString
      
      // create an actor with the uuid of the user
      system.actorOf(Props(new ProbeWrapper(probe)), uuid)
      
      // Fire off the message...
      stockActor ! WatchStock(uuid)
      
      // the userActor will be added as a watcher and get a message with the stock history
      val userActorMessage = probe.receiveOne(500.millis)
      userActorMessage must beAnInstanceOf[StockHistory]
    }
  }

  "A StocksActor" should {
    val symbol = "ABC"

    "a SetupStock message should send a StockHistory message to the user" in {
      val probe = new TestProbe(system)
      val uuid = UUID.randomUUID().toString
      val stockHolderActor = system.actorOf(Props[StocksActor])

      // create an actor with the uuid of the user
      system.actorOf(Props(new ProbeWrapper(probe)), uuid)
      
      // Fire off the message...
      stockHolderActor ! SetupStock(uuid, symbol)
      
      // Should create a new stockActor as a child and send it the stock history
      val stockHistory = probe.receiveOne(500 millis)
      stockHistory must beAnInstanceOf[StockHistory]
    }

  }
}
