import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport
import services.session.{ ClusterSystem, SessionCache }

class Module extends AbstractModule with AkkaGuiceSupport {
  override def configure(): Unit = {
    bind(classOf[ClusterSystem]).asEagerSingleton()
    bindTypedActor(SessionCache(), "replicatedCache")
  }
}
