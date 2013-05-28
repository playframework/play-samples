package actors

import akka.actor._
import akka.testkit._

import org.specs2.mutable._
import org.specs2.time.NoTimeConversions

import scala.concurrent.duration._
import scala.collection.immutable.Queue

import utils.{StockQuote, Global}

class StockActorSpec extends TestkitExample with Specification with NoTimeConversions {

  /*
   * Running tests in parallel (which would ordinarily be the default) will work only if no
   * shared resources are used (e.g. top-level actors with the same name or the
   * system.eventStream).
   *
   * It's usually safer to run the tests sequentially.
   */
  sequential

  final class StockActorWithStockQuote(symbol:String, price:Double) extends StockActor(symbol) {
    override lazy val stockQuote = new StockQuote {
      def newPrice(lastPrice: java.lang.Double): java.lang.Double = price
    }
  }

  "A StockActor" should {
    val symbol = "ABC"

    "send a StockUpdate message to the usersActor when receiving FetchLatest" in {
      // Create a stock actor with a stubbed out stockquote price.
      val price = 1234.0
      val stockActor = system.actorOf(Props(new StockActorWithStockQuote(symbol, price)))

      // Create a probe to serve as the "usersActor"
      val probe = new TestProbe(system)
      Global.usersActor = probe.ref

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
      actualStockHistory must beAnInstanceOf[Queue[java.lang.Double]]
    }
  }

  "A StockHolderActor" should {
    val symbol = "ABC"

    "create a new StockActor when receiving SetupStock" in {
      val stockHolderActor = system.actorOf(Props(new StockHolderActor))

      // Fire off the message...
      stockHolderActor ! SetupStock(symbol)

      // Should create a new stockActor as a child and return it to sender.
      val actualStockActor = receiveOne(500 millis)
      // We expect an ActorRef back.
      actualStockActor must beAnInstanceOf[ActorRef]
      // The name of the new StockActor must be the same as the symbol passed in.
      actualStockActor.asInstanceOf[ActorRef].path.name must ===(symbol)
    }

    "send FetchLatest to its children when receiving FetchLatest" in {

      // Override the stockholder actor to create a probe wrapper as a child.
      val probe = new TestProbe(system)
      final class StockHolderActorWithProbe extends StockHolderActor {
        context.actorOf(Props(new ProbeWrapper(probe)), symbol)
      }
      val stockHolderActor = system.actorOf(Props(new StockHolderActorWithProbe()))

      // Fire off the message...
      stockHolderActor ! FetchLatest

      // ...and ask the probe if it got the message.
      val actualMessage = probe.receiveOne(500 millis)
      actualMessage must ===(FetchLatest)
    }
  }
}
