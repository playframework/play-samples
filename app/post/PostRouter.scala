package post

import javax.inject.Inject

import com.lightbend.blog.comment._
import com.lightbend.blog.post._
import play.api.cache.Cached
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc._
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

import scala.concurrent.Future

/**
 * Takes HTTP requests and produces JSON or HTML responses
 * from a repository providing data.
 */
class PostRouter @Inject()(cached: Cached,
                           action: PostAction,
                           postRepository: PostRepository,
                           commentRepository: CommentRepository)
  extends SimpleRouter with AcceptExtractors with Rendering {

  // A trampoline ties an execution context to the currently running thread.
  // It should only be used for short running bits of non-blocking code.
  import play.api.libs.iteratee.Execution.Implicits.trampoline

  private val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)

  private val cacheDuration = 500

  override def routes: Routes = {

    case GET(p"/") =>
      cached.status(rh => s"${rh.method} ${rh.uri}", Status.OK, cacheDuration) {
        action.async { implicit request =>
          renderPosts()
        }
      }

    case HEAD(p"/") =>
      action.async { implicit request =>
        renderPosts()
      }

    case GET(p"/$id") =>
      cached.status(rh => s"${rh.method} ${rh.uri}", Status.OK, cacheDuration) {
        action.async { implicit request =>
          renderPost(PostId(id))
        }
      }

    case HEAD(p"/$id") =>
      action.async { implicit request =>
        renderPost(PostId(id))
      }
  }

  // https://www.playframework.com/documentation/2.5.x/ScalaContentNegotiation

  private def renderPost[A](id: PostId)(implicit request: PostRequest[A]): Future[Result] = {
    logger.trace("renderPost: ")
    // Find a single item from the repository
    render.async {

      case Accepts.Json() if request.method == "GET" =>
        // Query the repository for post with this id
        postRepository.get(id).flatMap {
          case Some(p) =>
            findComments(p.id).map { comments =>
              val post = Post(p, comments)
              val json = Json.toJson(post)
              Results.Ok(json)
            }
          case None =>
            Future.successful(Results.NotFound)
        }

      case Accepts.Html() if request.method == "GET" =>
        // Query the repository for post with this id
        postRepository.get(id).flatMap {
          case Some(p) =>
            findComments(p.id).map { comments =>
              val post = Post(p, comments)
              Results.Ok(views.html.posts.show(post))
            }
          case None =>
            Future.successful(Results.NotFound)
        }

      case Accepts.Json() & Accepts.Html() if request.method == "HEAD" =>
        postRepository.get(id).flatMap {
          case Some(p) =>
            Future.successful(Results.Ok)
          case None =>
            Future.successful(Results.NotFound)
        }

    }
  }

  private def renderPosts[A]()(implicit request: PostRequest[A]): Future[Result] = {
    render.async {

      case Accepts.Json() if request.method == "GET" =>
        // Query the repository for available posts
        postRepository.list().flatMap { postDataList =>
          findPosts(postDataList).map { posts =>
            val json = Json.toJson(posts)
            Results.Ok(json)
          }
        }

      case Accepts.Html() if request.method == "GET" =>
        // Query the repository for available posts
        postRepository.list().flatMap { postDataList =>
          findPosts(postDataList).map { posts =>
            Results.Ok(views.html.posts.index(posts))
          }
        }

      case Accepts.Json() & Accepts.Html() if request.method == "HEAD" =>
        // HEAD has no body, so just say hi
        Future.successful(Results.Ok)

    }
  }

  private def findPosts(postDataList: Iterable[PostData]): Future[Iterable[Post]] = {
    // Get an Iterable[Future[Post]] containing comments
    val listOfFutures = postDataList.map { p =>
      findComments(p.id).map { comments =>
        Post(p, comments)
      }
    }

    // Flip it into a single Future[Iterable[Post]]
    Future.sequence(listOfFutures)
  }

  private def findComments(postId: PostId): Future[Seq[Comment]] = {
    // Find all the comments for this post
    commentRepository.findByPost(postId.toString).map { comments =>
      comments.map(c => Comment(c.body)).toSeq
    }
  }

}
