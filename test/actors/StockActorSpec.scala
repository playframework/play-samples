package actors

import akka.actor._
import akka.testkit._

import org.specs2.mutable._
import org.specs2.time.NoTimeConversions

import scala.concurrent.duration._
import scala.collection.immutable.Queue

import utils.{StockQuote, Global}
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

  final class StockActorWithStockQuote(symbol:String, price:Double, watcher: ActorRef) extends StockActor(symbol) {
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
      val watcher = system.actorOf(Props(new ProbeWrapper(probe)))
      val stockActor = system.actorOf(Props(new StockActorWithStockQuote(symbol, price, watcher)))

      // Fire off the message...
      stockActor ! FetchLatest

      // ... and ask the probe if it got the StockUpdate message.
      val actualMessage = probe.receiveOne(500 millis)
      val expectedMessage = StockUpdate(symbol, price)
      actualMessage must === (expectedMessage)
    }
    "send the stockHistory to the sender when receiving FetchHistory" in {
      // Create a standard StockActor.
      val stockActor = system.actorOf(Props(new StockActor(symbol)))

      // Fire off the message...
      stockActor ! FetchHistory

      // This case uses 'sender' so we can use the ImplicitSender reference
      // to call receiveOne directly instead of using a test probe.
      val actualStockHistory = receiveOne(500 millis)
      actualStockHistory must beAnInstanceOf[StockHistory]
    }
    "add a watcher and send a FetchHistory message when receiving WatchStock" in {
      val probe = new TestProbe(system)
      
      // Create a standard StockActor.
      val stockActor = system.actorOf(Props(new StockActor(symbol)))

      val userActor = system.actorOf(Props(new ProbeWrapper(probe)))
      
      // Fire off the message...
      stockActor ! WatchStock(userActor)
      
      // the userActor will be added as a watcher and get a message with the stock history
      val userActorMessage = probe.receiveOne(500 millis)
      userActorMessage must beAnInstanceOf[StockHistory]
    }
  }

  "A StocksActor" should {
    val symbol = "ABC"

    "create a new StockActor or return the existing one when receiving SetupStock" in {
      val stockHolderActor = system.actorOf(Props[StocksActor])

      // Fire off the message...
      stockHolderActor ! SetupStock(symbol)

      // Should create a new stockActor as a child and send it the stock history
      val stockHistory = receiveOne(500 millis)
      stockHistory must beAnInstanceOf[StockHistory]
    }

  }
}
