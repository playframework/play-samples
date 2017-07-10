package actors

import javax.inject.Inject

import akka.actor._
import akka.event.LoggingReceive
import akka.stream.scaladsl._
import akka.util.Timeout
import play.api.Configuration
import play.api.libs.concurrent.InjectedActorSupport
import play.api.libs.json._
import stocks._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
 * Provide some DI and configuration sugar for new UserActor instances.
 */
class UserParentActor @Inject()(childFactory: UserActor.Factory,
                                configuration: Configuration)
                               (implicit ec: ExecutionContext)
  extends Actor with InjectedActorSupport with ActorLogging {

  import Messages._
  import UserParentActor._
  import akka.pattern.{ask, pipe}

  implicit val timeout = Timeout(2.seconds)

  private val defaultStocks = configuration.get[Seq[String]]("default.stocks").map(StockSymbol(_))

  override def receive: Receive = LoggingReceive {
    case Create(id) =>
      val name = s"userActor-$id"
      log.info(s"Creating user actor $name with default stocks $defaultStocks")
      val child: ActorRef = injectedChild(childFactory(id), name)
      val future = (child ? WatchStocks(defaultStocks.toSet)).mapTo[Flow[JsValue, JsValue, _]]
      pipe(future) to sender()
  }
}

object UserParentActor {
  case class Create(id: String)
}