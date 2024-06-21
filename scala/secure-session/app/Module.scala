import com.google.inject.AbstractModule
import play.api.libs.concurrent.PekkoGuiceSupport
import services.session.{ ClusterSystem, SessionCache }

class Module extends AbstractModule with PekkoGuiceSupport {
  override def configure(): Unit = {
    bind(classOf[ClusterSystem]).asEagerSingleton()
    bindTypedActor(SessionCache(), "replicatedCache")
  }
}
