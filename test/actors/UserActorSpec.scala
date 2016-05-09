package actors

import akka.actor._
import akka.testkit.{TestActorRef, _}
import com.typesafe.config.ConfigFactory
import org.scalatest.MustMatchers
import play.api.libs.json._

import scala.concurrent.duration._

class UserActorSpec extends TestKitSpec with MustMatchers {

  "UserActor" should {

    val symbol = "ABC"
    val price = 123
    val history = scala.collection.immutable.Seq[Double](0.1, 1.0)
    val configuration = play.api.Configuration.apply(ConfigFactory.parseString(
      """
        |default.stocks = ["GOOG", "AAPL", "ORCL"]
      """.stripMargin))

    "send a stock when receiving a StockUpdate message" in {
      val out = TestProbe()
      val stocksActor = TestProbe()

      val userActorRef = TestActorRef[UserActor](Props(new UserActor(out.ref, stocksActor.ref, configuration)))
      val userActor = userActorRef.underlyingActor

      // send off the stock update...
      userActor.receive(StockUpdate(symbol, price))

      // ...and expect it to be a JSON node.
      val jsObj: JsObject = out.receiveOne(500 millis).asInstanceOf[JsObject]
      jsObj \ "type" mustBe JsDefined(JsString("stockupdate"))
      jsObj \ "symbol" mustBe JsDefined(JsString(symbol))
      jsObj \ "price" mustBe JsDefined(JsNumber(price))
    }

    "send the stock history when receiving a StockHistory message" in {
      val out = TestProbe()
      val stocksActor = TestProbe()

      val userActorRef = TestActorRef[UserActor](Props(new UserActor(out.ref, stocksActor.ref, configuration)))
      val userActor = userActorRef.underlyingActor

      // send off the stock update...
      userActor.receive(StockHistory(symbol, history))
      val jsObj: JsObject = out.receiveOne(500 millis).asInstanceOf[JsObject]

      // ...and expect it to be a JSON node.
      jsObj \ "type" mustBe JsDefined(JsString("stockhistory"))
      jsObj \ "symbol" mustBe JsDefined(JsString("ABC"))
      jsObj \ "history" mustBe JsDefined(Json.arr(JsNumber(0.1), JsNumber(1.0)))
    }
  }

}
