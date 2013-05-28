package actors

import akka.actor._
import akka.testkit._

import org.specs2.mutable._
import org.specs2.time.NoTimeConversions
import org.specs2.matcher._

import org.codehaus.jackson.JsonNode

import scala.concurrent.duration._

class UserActorSpec extends TestkitExample with Specification with NoTimeConversions  {

  /*
   * Running tests in parallel (which would ordinarily be the default) will work only if no
   * shared resources are used (e.g. top-level actors with the same name or the
   * system.eventStream).
   *
   * It's usually safer to run the tests sequentially.
   */
  sequential

  "A user actor receiving StockUpdate" should {

    val uuid = java.util.UUID.randomUUID.toString
    val symbol = "ABC"
    val price = 123

    "write out a stock that is in the map" in {
      val out = new StubOut()
      val userActorRef = TestActorRef[UserActor](Props(new UserActor(uuid, out)))
      val userActor = userActorRef.underlyingActor

      // Tell the user actor that it has this stock in its internal map...
      val stockActorRef = TestActorRef[StockActor](Props(new StockActor(symbol)))
      userActor.stocks = Map(symbol -> stockActorRef)

      // send off the stock update...
      userActor.receive(StockUpdate(symbol, price))

      // ...and expect it to be a JSON node.
      val node = out.actual.toString
      node must /("type" -> "stockupdate")
      node must /("symbol" -> symbol)
      node must /("price" -> price)
    }

    "not write out a stock that is NOT in the map" in {
      val out = new StubOut()
      val userActorRef = TestActorRef[UserActor](Props(new UserActor(uuid, out)))
      val userActor = userActorRef.underlyingActor

      // send off the stock update...
      userActor.receive(StockUpdate(symbol, price))

      // ...and expect null.
      out.actual must beNull
    }
  }

  "A UserActor receiving WatchStock" should {
    val uuid = java.util.UUID.randomUUID.toString
    val symbol = "ABC"

    "write out to a web socket" in {
      import utils.Global._

      // Do a straight integration test here...
      val out = new StubOut()
      val userActorRef = system.actorOf(Props(new UserActor(uuid, out)))
      stockHolderActor = system.actorOf(Props(new StockHolderActor()))

      within (5 seconds) {
        userActorRef ! WatchStock(uuid, symbol)
        expectNoMsg() // block for 5 seconds
      }
      // Check that the node exists.
      val node = out.actual.toString
      node must /("type" -> "stockhistory")
      node must /("symbol" -> symbol)
    }
  }

  "A UserActor receiving UnwatchStock" should {

    val uuid = java.util.UUID.randomUUID.toString
    val symbol = "ABC"

    "remove the stock" in {
      val out = new StubOut()

      val userActorRef = TestActorRef[UserActor](Props(new UserActor(uuid, out)))
      val userActor = userActorRef.underlyingActor
      val stockActorRef = TestActorRef[StockActor](Props(new StockActor(symbol)))

      userActor.stocks = Map(symbol -> stockActorRef)

      userActor.receive(UnwatchStock(uuid, symbol))

      userActor.stocks must not haveKey(symbol)
    }
  }

}
