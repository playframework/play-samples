package test

import play.api.test.FakeRequest
import play.api.test.PlaySpecification
import play.api.test.WithApplication

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
class ApplicationSpec extends PlaySpecification {

  "Application" should {

    "send 404 on a bad request" in new WithApplication {
      override def running() = {
        val result = route(app, FakeRequest(GET, "/boum")).get
        status(result) mustEqual NOT_FOUND
      }
    }

    "render the index page" in new WithApplication {
      override def running() = {
        val home = route(app, FakeRequest(GET, "/")).get

        status(home) mustEqual OK
        contentType(home) must beSome[String].which(_ == "text/html")
        contentAsString(home) must contain("kitty cat")
      }
    }
  }
}
