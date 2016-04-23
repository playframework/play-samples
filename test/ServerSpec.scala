import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play._
import play.api.ApplicationLoader.Context
import play.api.BuiltInComponentsFromContext
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.mvc.Results
import play.api.test.Helpers._

class ServerSpec extends PlaySpec
  with OneServerPerSuiteWithComponents[GreetingComponents with AhcWSComponents]
  with Results
  with ScalaFutures {

  override def createComponents(context: Context) = new GreetingComponents(context) with AhcWSComponents

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

