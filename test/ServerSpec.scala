import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play._
import play.api.mvc.Results
import play.api.test.Helpers._

class ServerSpec extends PlaySpec
  with OneServerPerSuiteWithMyComponents
  with Results
  with ScalaFutures {

  "Server query should" should {

    "work" in {
      implicit val ec = app.materializer.executionContext
      val wsClient = components.wsClient

      whenReady(wsUrl("/")(portNumber, wsClient).get) { response =>
        response.status mustBe OK
      }
    }

  }

}

