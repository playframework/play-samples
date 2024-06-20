package controllers

import javax.inject._

import actors._
import org.apache.pekko.NotUsed
import org.apache.pekko.actor.typed.{ ActorRef, Scheduler }
import org.apache.pekko.actor.typed.scaladsl.AskPattern._
import org.apache.pekko.stream.scaladsl._
import org.apache.pekko.util.Timeout
import org.webjars.play.WebJarsUtil
import play.api.Logger
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.duration._
import scala.concurrent.{ ExecutionContext, Future }

/**
 * This class creates the actions and the websocket needed.
 */
@Singleton
class HomeController @Inject()(userParentActor: ActorRef[UserParentActor.Create], webJarsUtil: org.webjars.play.WebJarsUtil,
                               cc: ControllerComponents)
                              (implicit ec: ExecutionContext, scheduler: Scheduler)
  extends AbstractController(cc) with SameOriginCheck {

  val logger = play.api.Logger(getClass)

  // Home page that renders template
  def index = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index(webJarsUtil))
  }

  /**
   * Creates a websocket.  `acceptOrResult` is preferable here because it returns a
   * Future[Flow], which is required internally.
   *
   * @return a fully realized websocket.
   */
  def ws: WebSocket = WebSocket.acceptOrResult[JsValue, JsValue] {
    case rh if sameOriginCheck(rh) =>
      wsFutureFlow(rh).map { flow =>
        Right(flow)
      }.recover {
        case e: Exception =>
          logger.error("Cannot create websocket", e)
          val jsError = Json.obj("error" -> "Cannot create websocket")
          val result = InternalServerError(jsError)
          Left(result)
      }

    case rejected =>
      logger.error(s"Request ${rejected} failed same origin check")
      Future.successful {
        Left(Forbidden("forbidden"))
      }
  }

  /**
   * Creates a Future containing a Flow of JsValue in and out.
   */
  private def wsFutureFlow(request: RequestHeader): Future[Flow[JsValue, JsValue, NotUsed]] = {
    // Use guice assisted injection to instantiate and configure the child actor.
    implicit val timeout: Timeout = Timeout(1.second) // the first run in dev can take a while :-(
    userParentActor.ask(replyTo => UserParentActor.Create(request.id.toString, replyTo))
  }

}

trait SameOriginCheck {

  def logger: Logger

  /**
   * Checks that the WebSocket comes from the same origin.  This is necessary to protect
   * against Cross-Site WebSocket Hijacking as WebSocket does not implement Same Origin Policy.
   *
   * See https://tools.ietf.org/html/rfc6455#section-1.3 and
   * http://blog.dewhurstsecurity.com/2013/08/30/security-testing-html5-websockets.html
   */
  def sameOriginCheck(rh: RequestHeader): Boolean = {
    rh.headers.get("Origin") match {
      case Some(originValue) if originMatches(originValue) =>
        logger.debug(s"originCheck: originValue = $originValue")
        true

      case Some(badOrigin) =>
        logger.error(s"originCheck: rejecting request because Origin header value ${badOrigin} is not in the same origin")
        false

      case None =>
        logger.error("originCheck: rejecting request because no Origin header found")
        false
    }
  }

  /**
   * Returns true if the value of the Origin header contains an acceptable value.
   *
   * This is probably better done through configuration same as the allowedhosts filter.
   */
  def originMatches(origin: String): Boolean = {
    origin.contains("localhost:9000") || origin.contains("localhost:19001")
  }

}
