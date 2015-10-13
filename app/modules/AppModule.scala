package modules

import javax.inject.Inject

import com.example.user.{UserDAO, SlickUserModule}
import com.google.inject.AbstractModule
import com.typesafe.config.Config
import play.api.inject.ApplicationLifecycle

import play.api.{Environment, Configuration}

import scala.concurrent.Future

class AppModule(environment: Environment,
                configuration: Configuration) extends AbstractModule {
  override def configure(): Unit = {

    bind(classOf[Config]).toInstance(configuration.underlying)

    install(new SlickUserModule)
    bind(classOf[UserDAOCloseHook]).asEagerSingleton()
  }
}

/**
 * Closes database connections safely.  Important on dev restart.
 */
class UserDAOCloseHook @Inject() (dao: UserDAO, lifecycle: ApplicationLifecycle) {
  private val logger = org.slf4j.LoggerFactory.getLogger("application")

  lifecycle.addStopHook { () =>
    Future.successful {
      logger.info("Now closing database connections!")
      dao.close()
    }
  }
}
