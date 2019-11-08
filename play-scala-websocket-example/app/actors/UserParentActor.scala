package actors

import javax.inject.Inject

import akka.actor.typed.{ ActorRef, Scheduler }
import akka.actor.{ Actor, ActorLogging }
import akka.event.LoggingReceive
import akka.util.Timeout
import play.api.Configuration
import stocks._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
 * Provide some DI and configuration sugar for new UserActor instances.
 */
class UserParentActor @Inject()(childFactory: UserActor.Factory, configuration: Configuration)(
    implicit ec: ExecutionContext, scheduler: Scheduler)
  extends Actor with ActorLogging {

  import UserActor.WatchStocks
  import UserParentActor._
  import akka.pattern.pipe
  import akka.actor.typed.scaladsl.AskPattern._
  import akka.actor.typed.scaladsl.adapter._

  implicit val timeout = Timeout(2.seconds)

  private val defaultStocks = configuration.get[Seq[String]]("default.stocks").map(StockSymbol(_))

  override def receive: Receive = LoggingReceive {
    case Create(id) =>
      val name = s"userActor-$id"
      log.info(s"Creating user actor $name with default stocks $defaultStocks")
      val child: ActorRef[UserActor.Message] = context.system.spawn(childFactory(id), name)
      val future = child.ask(replyTo => WatchStocks(defaultStocks.toSet, replyTo))
      pipe(future) to sender()
  }
}

object UserParentActor {
  case class Create(id: String)
}
