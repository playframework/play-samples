package controller

import org.scalatestplus.play.{BaseOneAppPerSuite, PlaySpec}
import play.api.test.FakeRequest
import play.api.test.Helpers._

/**
 * Runs a functional test with the application, using an in memory
 * database.  Migrations are handled automatically by play-flyway
 */
class FunctionalSpec extends PlaySpec with BaseOneAppPerSuite with MyApplicationFactory {

  "HomeController" should {

    "work with in memory h2 database" in {
      val future = route(app, FakeRequest(GET, "/")).get
      contentAsString(future) must include("myuser@example.com")
    }
  }

}
