package actors

import org.apache.pekko.NotUsed
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.actor.typed.{ ActorRef, Behavior, Scheduler }
import org.apache.pekko.stream.scaladsl.Flow
import org.apache.pekko.util.Timeout
import com.google.inject.Provides
import play.api.Configuration
import play.api.libs.concurrent.ActorModule
import play.api.libs.json.JsValue
import stocks._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
 * Provide some DI and configuration sugar for new UserActor instances.
 */
object UserParentActor extends ActorModule {
  type Message = Create

  final case class Create(id: String, replyTo: ActorRef[Flow[JsValue, JsValue, NotUsed]])

  @Provides def apply(childFactory: UserActor.Factory, configuration: Configuration)
      (implicit ec: ExecutionContext, scheduler: Scheduler): Behavior[Create] = {

    implicit val timeout: Timeout = Timeout(2.seconds)

    val defaultStocks = configuration.get[Seq[String]]("default.stocks").map(StockSymbol(_))

    Behaviors.setup { context =>
      Behaviors.logMessages {
        Behaviors.receiveMessage {
          case Create(id, replyTo) =>
            val name = s"userActor-$id"
            context.log.info(s"Creating user actor $name with default stocks $defaultStocks")
            val child = context.spawn(childFactory(id), name)
            child ! UserActor.WatchStocks(defaultStocks.toSet, replyTo)
            Behaviors.same
        }
      }
    }
  }
}
