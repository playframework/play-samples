import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._

@RunWith(classOf[JUnitRunner])
class IntegrationSpec extends Specification {

  "Application" should {

    "send 404 on a bad request" in new WithApplication {
      override def running() =
        status(route(app, FakeRequest(GET, "/boum")).get) must_== NOT_FOUND
    }

    "render the index page" in new WithApplication {
      override def running() = {
        val home = route(app, FakeRequest(GET, "/")).get

        status(home) must equalTo(OK)
        contentType(home) must beSome[String].which(_ == "text/html")
      }
    }
  }
}
