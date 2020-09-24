package controllers

import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test.FakeRequest
import play.api.test.Helpers._

/**
 * Run an integration test to ensure the controller works
 */
class ScalaCommentControllerSpec extends PlaySpec
  with GuiceOneAppPerSuite
  with ScalaFutures
{

  "comment controller" should {
    "return OK through route" in {
      val request = FakeRequest(method = GET, path = "/scala/comet/liveClock")
      route(app, request) match {
        case Some(future) =>
          whenReady(future) { result =>
            result.header.status mustEqual(OK)
          }
        case None =>
          fail()
      }
    }
  }
}
