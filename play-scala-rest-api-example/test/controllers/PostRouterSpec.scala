import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.libs.json.{ JsResult, Json }
import play.api.mvc.{ RequestHeader, Result }
import play.api.test._
import play.api.test.Helpers._
import play.api.test.CSRFTokenHelper._
import v1.post.PostResource

import scala.concurrent.Future

class PostRouterSpec extends PlaySpec with GuiceOneAppPerTest {

  "PostRouter" should {

    "render the list of posts" in {
      val request = FakeRequest(GET, "/v1/posts").withHeaders(HOST -> "localhost:9000").withCSRFToken
      val home:Future[Result] = route(app, request).get

      val posts: Seq[PostResource] = Json.fromJson[Seq[PostResource]](contentAsJson(home)).get
      posts.filter(_.id == "1").head mustBe (PostResource("1","/v1/posts/1", "title 1", "blog post 1" ))
    }

    "render the list of posts when url ends with a trailing slash" in {
      val request = FakeRequest(GET, "/v1/posts/").withHeaders(HOST -> "localhost:9000").withCSRFToken
      val home:Future[Result] = route(app, request).get

      val posts: Seq[PostResource] = Json.fromJson[Seq[PostResource]](contentAsJson(home)).get
      posts.filter(_.id == "1").head mustBe (PostResource("1","/v1/posts/1", "title 1", "blog post 1" ))
    }
  }

}