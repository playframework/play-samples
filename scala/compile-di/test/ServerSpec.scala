import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.play._

/**
 * Runs a play server on a random port (Helpers.testServerPort == 0).
 */
class ServerSpec extends PlaySpec
  with BaseOneServerPerSuite
  with MyApplicationFactory
  with ScalaFutures
  with IntegrationPatience {

  private implicit val implicitPort: Int = port

  "Server query should" should {
    "work" in {
      whenReady(play.api.test.WsTestClient.wsUrl("/").get()) { response =>
        response.status mustBe play.api.http.Status.OK
      }
    }
  }
}

