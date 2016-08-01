package post

import javax.inject.Inject

import com.lightbend.blog.post.PostId
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

/**
 * The default action for a post request.
 *
 * This is the place to put logging, metrics, and general custom headers.
 */
class PostAction @Inject()(val messagesApi: MessagesApi)(implicit ec: ExecutionContext) extends ActionBuilder[PostRequest] {
  private val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)

  type PostRequestBlock[A] = (PostRequest[A]) => Future[Result]

  override def invokeBlock[A](request: Request[A], block: PostRequestBlock[A]): Future[Result] = {
    val messages = messagesApi.preferred(request)
    val postRequest = new PostRequest(request, messages)
    block(postRequest).map { result =>
      if (logger.isTraceEnabled) {
        logger.trace(s"postAction: request = $request, result = ${result.header}")
      }
      result
    }
  }

}

/**
 * A wrapped request that can contain Post information.
 *
 * This is commonly used to hold request-specific information like
 * security credentials and localized Messages
 */
class PostRequest[A](request: Request[A], val messages: Messages) extends WrappedRequest(request) {
  override def toString(): String = s"[${request.uri}]"
}
