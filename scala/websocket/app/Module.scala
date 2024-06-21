import javax.inject.{ Inject, Provider, Singleton }

import actors._
import org.apache.pekko.actor.typed.{ ActorRef, Behavior }
import org.apache.pekko.stream.Materializer
import com.google.inject.AbstractModule
import play.api.libs.concurrent.PekkoGuiceSupport

import scala.concurrent.ExecutionContext

class Module extends AbstractModule with PekkoGuiceSupport {
  override def configure(): Unit = {
    bindTypedActor(StocksActor(), "stocksActor")
    bindTypedActor(UserParentActor, "userParentActor")
    bind(classOf[UserActor.Factory]).toProvider(classOf[UserActorFactoryProvider])
  }
}

@Singleton
class UserActorFactoryProvider @Inject()(
    stocksActor: ActorRef[StocksActor.GetStocks],
    mat: Materializer,
    ec: ExecutionContext,
) extends Provider[UserActor.Factory] {
  def get() = UserActor(_, stocksActor)(mat, ec)
}
