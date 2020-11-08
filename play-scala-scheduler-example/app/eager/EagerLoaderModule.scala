package eager
import java.util.TimeZone

import com.google.inject.AbstractModule
import schedulerModule.{TaskActor, TaskScheduler}
import play.api.libs.concurrent.AkkaGuiceSupport

// A Module is needed to register bindings.

class EagerLoaderModule extends AbstractModule{

    override def configure(): Unit = {
        bind(classOf[TaskScheduler]).asEagerSingleton()
    }

}
