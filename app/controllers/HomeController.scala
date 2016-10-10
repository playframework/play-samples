package controllers

import javax.inject._

import akka.NotUsed
import akka.actor.ActorSystem
import akka.event.Logging
import akka.stream.Materializer
import akka.stream.scaladsl.{BroadcastHub, Flow, Keep, MergeHub}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

/**
 * A very simple chat client using websockets.
 */
@Singleton
class HomeController @Inject()(implicit actorSystem: ActorSystem,
                               mat: Materializer,
                               executionContext: ExecutionContext)
  extends Controller {

  type WSMessage = String

  private val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)

  private implicit val logging = Logging(actorSystem.eventStream, logger.getName)

  // chat room many clients -> merge hub -> broadcasthub -> many clients
  private val (chatSink, chatSource) = {
    val source = MergeHub.source[WSMessage].log("source")
    val sink = BroadcastHub.sink[WSMessage]
    source.toMat(sink)(Keep.both).run()
  }

  private val userFlow: Flow[WSMessage, WSMessage, NotUsed] = {
    Flow[WSMessage].via(Flow.fromSinkAndSource(chatSink, chatSource)).log("userFlow")
  }

  def index: Action[AnyContent] = Action { implicit request =>
    //val url = routes.HomeController.chat().webSocketURL()
    val url = "ws://localhost:9000/chat"
    Ok(views.html.index(url))
  }

  def chat: WebSocket = WebSocket.acceptOrResult[WSMessage, WSMessage] {
    case rh if sameOriginCheck(rh) =>
      Future.successful(userFlow).map { flow =>
        Right(flow)
      }.recover {
        case e: Exception =>
          logger.error("Cannot create websocket", e)
          val error = "Cannot create websocket"
          val result = InternalServerError(error)
          Left(result)
      }

    case rejected =>
      logger.error(s"Request ${rejected} failed same origin check")
      Future.successful {
        Left(Forbidden("forbidden"))
      }
  }

  /**
   * Checks that the WebSocket comes from the same origin.  This is necessary to protect
   * against Cross-Site WebSocket Hijacking as WebSocket does not implement Same Origin Policy.
   *
   * See https://tools.ietf.org/html/rfc6455#section-1.3 and
   * http://blog.dewhurstsecurity.com/2013/08/30/security-testing-html5-websockets.html
   */
  private def sameOriginCheck(rh: RequestHeader): Boolean = {
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
   */
  private def originMatches(origin: String): Boolean = {
    origin.contains("localhost:9000") || origin.contains("localhost:19001")
  }

}
