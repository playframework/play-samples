import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._

@RunWith(classOf[JUnitRunner])
class IntegrationSpec extends Specification {

  "Application" should {

    "send 404 on a bad request" in new WithApplication {
      route(app, FakeRequest(GET, "/boum")) must beSome.which (status(_) == NOT_FOUND)
    }

    "render the index page" in new WithApplication {
      val home = route(app, FakeRequest(GET, "/")).get

      status(home) must equalTo(OK)
      contentType(home) must beSome.which(_ == "text/html")
    }
  }
}
