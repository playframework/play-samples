import akka.actor.ActorSystem
import akka.actor.typed.Scheduler
import com.google.inject.AbstractModule
import javax.inject.{ Inject, Provider, Singleton }
import play.api.libs.concurrent.AkkaGuiceSupport
import services.session.{ ClusterSystem, SessionCache }

class Module extends AbstractModule with AkkaGuiceSupport {
  override def configure(): Unit = {
    bind(classOf[ClusterSystem]).asEagerSingleton()
    bindTypedActor(SessionCache.create(), "replicatedCache")
    bind(classOf[Scheduler]).toProvider(classOf[AkkaSchedulerProvider])
  }
}

// Drop once upgraded to Play 2.8.0-RC1
@Singleton class AkkaSchedulerProvider @Inject() (system: ActorSystem) extends Provider[Scheduler] {
  import akka.actor.typed.scaladsl.adapter._
  override lazy val get: Scheduler = system.toTyped.scheduler
}
