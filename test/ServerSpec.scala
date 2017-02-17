import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play._
import play.api.libs.ws.WSClient
import play.api.mvc.Results
import play.api.test.Injecting

/**
 * Runs a play server on the default test port (Helpers.testServerPort == 19001).
 */
class ServerSpec extends PlaySpec
  with BaseOneServerPerSuite
  with MyApplicationFactory
  with Injecting
  with ScalaFutures {

  implicit val wsClient: WSClient = inject[WSClient]

  "Server query should" should {
    "work" in {
      whenReady(wsUrl("/").get) { response =>
        response.status mustBe play.api.http.Status.OK
      }
    }

  }

}

