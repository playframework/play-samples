package controllers

import javax.inject._

import actors._
import akka.NotUsed
import akka.actor._
import akka.event.Logging
import akka.pattern.ask
import akka.stream._
import akka.stream.scaladsl._
import akka.util.Timeout
import org.reactivestreams.Publisher
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

/**
 * This class creates the actions and the websocket needed.
 */
@Singleton
class HomeController @Inject()(@Named("stocksActor") stocksActor: ActorRef,
                               @Named("userParentActor") userParentActor: ActorRef)
                              (implicit actorSystem: ActorSystem,
                               mat: Materializer,
                               ec: ExecutionContext) extends Controller {

  // Use a direct reference to SLF4J
  private val logger = org.slf4j.LoggerFactory.getLogger("controllers.HomeController")

  // Home page that renders template
  def index = Action { implicit request =>
    Ok(views.html.index())
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
   */
  def originMatches(origin: String): Boolean = {
    origin.contains("localhost:9000") || origin.contains("localhost:19001")
  }

  /**
   * Creates a Future containing a Flow of JsValue in and out.
   */
  def wsFutureFlow(request: RequestHeader): Future[Flow[JsValue, JsValue, NotUsed]] = {
    // create an actor ref source and associated publisher for sink
    val (webSocketOut: ActorRef, webSocketIn: Publisher[JsValue]) = createWebSocketConnections()

    // Create a user actor off the request id and attach it to the source
    val userActorFuture = createUserActor(request.id.toString, webSocketOut)

    // Once we have an actor available, create a flow...
    userActorFuture.map { userActor =>
      createWebSocketFlow(webSocketIn, userActor)
    }
  }

  /**
   * Creates a materialized flow for the websocket, exposing the source and sink.
   *
   * @return the materialized input and output of the flow.
   */
  def createWebSocketConnections(): (ActorRef, Publisher[JsValue]) = {

    // Creates a source to be materialized as an actor reference.
    val source: Source[JsValue, ActorRef] = {
      // If you want to log on a flow, you have to use a logging adapter.
      // http://doc.akka.io/docs/akka/2.4.4/scala/logging.html#SLF4J
      val logging = Logging(actorSystem.eventStream, logger.getName)

      // Creating a source can be done through various means, but here we want
      // the source exposed as an actor so we can send it messages from other
      // actors.
      Source.actorRef[JsValue](10, OverflowStrategy.dropTail).log("actorRefSource")(logging)
    }

    // Creates a sink to be materialized as a publisher.  Fanout is false as we only want
    // a single subscriber here.
    val sink: Sink[JsValue, Publisher[JsValue]] = Sink.asPublisher(fanout = false)

    // Connect the source and sink into a flow, telling it to keep the materialized values,
    // and then kicks the flow into existence.
    source.toMat(sink)(Keep.both).run()
  }

  /**
   * Creates a flow of events from the websocket to the user actor.
   *
   * When the flow is terminated, the user actor is no longer needed and is stopped.
   *
   * @param userActor   the user actor receiving websocket events.
   * @param webSocketIn the "read" side of the websocket, that publishes JsValue to UserActor.
   * @return a Flow of JsValue in both directions.
   */
  def createWebSocketFlow(webSocketIn: Publisher[JsValue], userActor: ActorRef): Flow[JsValue, JsValue, NotUsed] = {
    // http://doc.akka.io/docs/akka/current/scala/stream/stream-flows-and-basics.html#stream-materialization
    // http://doc.akka.io/docs/akka/current/scala/stream/stream-integrations.html#integrating-with-actors

    // source is what comes in: browser ws events -> play -> publisher -> userActor
    // sink is what comes out:  userActor -> websocketOut -> play -> browser ws events
    val flow = {
      val sink = Sink.actorRef(userActor, akka.actor.Status.Success(()))
      val source = Source.fromPublisher(webSocketIn)
      Flow.fromSinkAndSource(sink, source)
    }

    // Unhook the user actor when the websocket flow terminates
    // http://doc.akka.io/docs/akka/current/scala/stream/stages-overview.html#watchTermination
    val flowWatch: Flow[JsValue, JsValue, NotUsed] = flow.watchTermination() { (_, termination) =>
      termination.foreach { done =>
        logger.info(s"Terminating actor $userActor")
        stocksActor.tell(UnwatchStock(None), userActor)
        actorSystem.stop(userActor)
      }
      NotUsed
    }

    flowWatch
  }

  /**
   * Creates a user actor with a given name, using the websocket out actor for output.
   *
   * @param name         the name of the user actor.
   * @param webSocketOut the "write" side of the websocket, that the user actor sends JsValue to.
   * @return a user actor for this ws connection.
   */
  def createUserActor(name: String, webSocketOut: ActorRef): Future[ActorRef] = {
    // Use guice assisted injection to instantiate and configure the child actor.
    val userActorFuture = {
      implicit val timeout = Timeout(100.millis)
      (userParentActor ? UserParentActor.Create(name, webSocketOut)).mapTo[ActorRef]
    }
    userActorFuture
  }

}
