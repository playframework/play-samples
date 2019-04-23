import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport
import services.session.{ ClusterSystem, SessionCache }

class Module extends AbstractModule with AkkaGuiceSupport {
  def configure(): Unit = {
    bind(classOf[ClusterSystem]).asEagerSingleton()
    bindActor[SessionCache]("replicatedCache")
  }
}
