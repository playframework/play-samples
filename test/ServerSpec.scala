import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play._
import play.api.Application
import play.api.mvc.Results
import play.api.test.Helpers._

class ServerSpec extends PlaySpec
  with Results
  with CompileTimeComponents
  with OneServerPerSuite
  with ScalaFutures {

  override implicit lazy val app: Application = components.application

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

