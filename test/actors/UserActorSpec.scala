package actors

import akka.actor._
import akka.testkit._

import org.specs2.mutable._

import scala.concurrent.duration._

import scala.collection.JavaConverters._
import play.api.test.WithApplication
import org.specs2.matcher.JsonMatchers

class UserActorSpec extends TestkitExample with SpecificationLike with JsonMatchers {

  /*
   * Running tests in parallel (which would ordinarily be the default) will work only if no
   * shared resources are used (e.g. top-level actors with the same name or the
   * system.eventStream).
   *
   * It's usually safer to run the tests sequentially.
   */

  sequential

  "UserActor" should {

    val symbol = "ABC"
    val price = 123
    val history = List[java.lang.Double](0.1, 1.0).asJava

    "send a stock when receiving a StockUpdate message" in new WithApplication {
      val out = new StubOut()

      val userActorRef = TestActorRef[UserActor](Props(new UserActor(out)))
      val userActor = userActorRef.underlyingActor

      // send off the stock update...
      userActor.receive(StockUpdate(symbol, price))

      // ...and expect it to be a JSON node.
      val node = out.actual.toString
      node must /("type" -> "stockupdate")
      node must /("symbol" -> symbol)
      node must /("price" -> price)
    }

    "send the stock history when receiving a StockHistory message" in new WithApplication {
      val out = new StubOut()

      val userActorRef = TestActorRef[UserActor](Props(new UserActor(out)))
      val userActor = userActorRef.underlyingActor

      // send off the stock update...
      userActor.receive(StockHistory(symbol, history))

      // ...and expect it to be a JSON node.
      out.actual.get("type").asText must beEqualTo("stockhistory")
      out.actual.get("symbol").asText must beEqualTo(symbol)
      out.actual.get("history").get(0).asDouble must beEqualTo(history.get(0))
    }
  }

}
