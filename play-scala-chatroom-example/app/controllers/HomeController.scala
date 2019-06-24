package controllers

import java.net.URI
import javax.inject._

import akka.actor.ActorSystem
import akka.event.Logging
import akka.stream.Materializer
import akka.stream.scaladsl.{BroadcastHub, Flow, Keep, MergeHub, Source}
import play.api.Logger
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

/**
 * A very simple chat client using websockets.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents, inputSanitizer: InputSanitizer)
                              (implicit actorSystem: ActorSystem,
                               mat: Materializer,
                               executionContext: ExecutionContext,
                               webJarsUtil: org.webjars.play.WebJarsUtil)
                               extends BaseController with RequestMarkerContext {

  private type WSMessage = String

  private val logger = Logger(getClass)

  private implicit val logging = Logging(actorSystem.eventStream, logger.underlyingLogger.getName)

  // chat room many clients -> merge hub -> broadcasthub -> many clients
  private val (chatSink, chatSource) = {
    // Don't log MergeHub$ProducerFailed as error if the client disconnects.
    // recoverWithRetries -1 is essentially "recoverWith"
    val source = MergeHub.source[WSMessage]
      .log("source")
      // Let's also do some input sanitization to avoid XSS attacks
      .map(inputSanitizer.sanitize)
      .recoverWithRetries(-1, { case _: Exception => Source.empty })

    val sink = BroadcastHub.sink[WSMessage]
    source.toMat(sink)(Keep.both).run()
  }

  private val userFlow: Flow[WSMessage, WSMessage, _] = {
    Flow.fromSinkAndSource(chatSink, chatSource)
  }

  def index: Action[AnyContent] = Action { implicit request: RequestHeader =>
    val webSocketUrl = routes.HomeController.chat().webSocketURL()
    logger.info(s"index: ")
    Ok(views.html.index(webSocketUrl))
  }

  def chat(): WebSocket = {
    WebSocket.acceptOrResult[WSMessage, WSMessage] {
      case rh if sameOriginCheck(rh) =>
        Future.successful(userFlow).map { flow =>
          Right(flow)
        }.recover {
          case e: Exception =>
            val msg = "Cannot create websocket"
            logger.error(msg, e)
            val result = InternalServerError(msg)
            Left(result)
        }

      case rejected =>
        logger.error(s"Request ${rejected} failed same origin check")
        Future.successful {
          Left(Forbidden("forbidden"))
        }
    }
  }

  /**
   * Checks that the WebSocket comes from the same origin.  This is necessary to protect
   * against Cross-Site WebSocket Hijacking as WebSocket does not implement Same Origin Policy.
   *
   * See https://tools.ietf.org/html/rfc6455#section-1.3 and
   * http://blog.dewhurstsecurity.com/2013/08/30/security-testing-html5-websockets.html
   */
  private def sameOriginCheck(implicit rh: RequestHeader): Boolean = {
    // The Origin header is the domain the request originates from.
    // https://tools.ietf.org/html/rfc6454#section-7
    logger.debug("Checking the ORIGIN ")

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
    try {
      val url = new URI(origin)
      url.getHost == "localhost" &&
        (url.getPort match { case 9000 | 19001 => true; case _ => false })
    } catch {
      case e: Exception => false
    }
  }

}
