package controller

import controllers.routes
import org.scalatestplus.play.{BaseOneAppPerSuite, PlaySpec}
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._

/**
 * Runs a functional test with the application, using an in memory
 * database.  Migrations are handled automatically by play-flyway
 */
class FunctionalSpec extends PlaySpec with BaseOneAppPerSuite with MyApplicationFactory {

  "UserController" should {
    "work with in memory h2 database" in {
      val futureGetIndex = route(app, FakeRequest(GET, "/")).get
      contentAsString(futureGetIndex) must include("myuser@example.com")
    }

    "adding a new email will work" in {
      val email = "myuser1@gmail.com"

      val futureCreateUser = route(app,FakeRequest(POST, "/users")
        .withFormUrlEncodedBody("email" -> email)).get
      status(futureCreateUser) mustBe SEE_OTHER
      redirectLocation(futureCreateUser) mustBe Some(routes.UserController.index.url)

      val futureGetIndex = route(app, FakeRequest(GET, "/")).get
      contentAsString(futureGetIndex) must include(email)
    }

    "editing an email will work" in {
      val email = "myuser2@gmail.com"

      val futureGetAllUserIds =  route(app,FakeRequest(GET,"/users/all")).get
      val bodyAsJson: JsValue = Json.parse(contentAsString(futureGetAllUserIds))
      val listOfIds: List[String] = bodyAsJson.as[List[String]]

      val futureUpdateUser = route(app,FakeRequest(POST, s"/users/${listOfIds.head}")
        .withFormUrlEncodedBody("email" -> email)).get

      status(futureUpdateUser) mustBe SEE_OTHER
      redirectLocation(futureUpdateUser) mustBe Some(routes.UserController.index.url)

      val futureIndex = route(app, FakeRequest(GET, "/")).get
      contentAsString(futureIndex) must include(email)
    }

    "deleting all users will work" in {
      val email = "myuser3@example.com"

      val futureCreateUser = route(app, FakeRequest(POST, "/users")
        .withFormUrlEncodedBody("email" -> email)).get
      status(futureCreateUser) mustBe SEE_OTHER
      redirectLocation(futureCreateUser) mustBe Some(routes.UserController.index.url)

      val futureInitialIndex = route(app, FakeRequest(GET, "/")).get
      contentAsString(futureInitialIndex) must include(email)

      val futureGetAllUserIds = route(app, FakeRequest(GET, "/users/all")).get
      val bodyAsJson: JsValue = Json.parse(contentAsString(futureGetAllUserIds))
      val listOfIds: List[String] = bodyAsJson.as[List[String]]

      listOfIds.foreach { eachId =>
        val futureDeleteUser = route(app, FakeRequest(GET, s"/users/$eachId/delete")).get
        status(futureDeleteUser) mustBe SEE_OTHER
        redirectLocation(futureDeleteUser) mustBe Some(routes.UserController.index.url)
      }

      val futureUpdatedIndex = route(app, FakeRequest(GET, "/")).get
      contentAsString(futureUpdatedIndex) mustNot include(email)
    }
  }
}
