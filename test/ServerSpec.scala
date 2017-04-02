import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play._
import play.api.libs.ws.WSClient
import play.api.test._

/**
 * Runs a play server on the default test port (Helpers.testServerPort == 19001).
 */
class ServerSpec extends PlaySpec
  with BaseOneServerPerSuite
  with MyApplicationFactory
  with ScalaFutures {

  implicit val intPort = Helpers.testServerPort

  val wsClient = WsTestClient

  "Server query should" should {
    "work" in {
      whenReady(wsClient.wsUrl("/").get) { response =>
        response.status mustBe play.api.http.Status.OK
      }
    }

  }

}

