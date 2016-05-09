package actors

import akka.actor._
import akka.testkit._

import scala.concurrent.duration._
import scala.collection.immutable.HashSet
import utils.StockQuote

class StockActorSpec extends TestKitSpec {

  final class StockActorWithStockQuote(symbol: String, price: Double, watcher: ActorRef) extends StockActor(symbol) {
    watchers = HashSet[ActorRef](watcher)
    override lazy val stockQuote = new StockQuote {
      def newPrice(lastPrice: Double): Double = price
    }
  }

  "WatchStock" should {
    val symbol = "ABC"

    "notify watchers when a new stock is received" in {
      // Create a stock actor with a stubbed out stockquote price and watcher
      val probe = new TestProbe(system)
      val price = 1234.0
      val stockActor = system.actorOf(Props(new StockActorWithStockQuote(symbol, price, probe.ref)))

      system.actorOf(Props(new ProbeWrapper(probe)))

      // Fire off the message...
      stockActor ! FetchLatest

      // ... and ask the probe if it got the StockUpdate message.
      val actualMessage = probe.receiveOne(500 millis)
      val expectedMessage = StockUpdate(symbol, price)
      actualMessage must ===(expectedMessage)
    }

    "add a watcher and send a StockHistory message to the user" in {
      val probe = new TestProbe(system)

      // Create a standard StockActor.
      val stockActor = system.actorOf(Props(new StockActor(symbol)))

      // create an actor which will test the UserActor
      val userActor = system.actorOf(Props(new ProbeWrapper(probe)))

      // Fire off the message, setting the sender as the UserActor
      // Simulates sending the message as if it was sent from the userActor
      stockActor.tell(WatchStock(symbol), userActor)

      // the userActor will be added as a watcher and get a message with the stock history
      val userActorMessage = probe.receiveOne(500.millis)
      userActorMessage mustBe a [StockHistory]
    }
  }

}
