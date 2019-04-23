import javax.inject.{Inject, Provider, Singleton}

import com.example.user.UserDAO
import com.example.user.slick.SlickUserDAO
import com.google.inject.AbstractModule
import com.typesafe.config.Config
import play.api.inject.ApplicationLifecycle
import play.api.{Configuration, Environment}
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.Future

/**
 * This module handles the bindings for the API to the Slick implementation.
 *
 * https://www.playframework.com/documentation/latest/ScalaDependencyInjection#Programmatic-bindings
 */
class Module(environment: Environment,
             configuration: Configuration) extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[Database]).toProvider(classOf[DatabaseProvider])
    bind(classOf[UserDAO]).to(classOf[SlickUserDAO])
    bind(classOf[UserDAOCloseHook]).asEagerSingleton()
  }
}

@Singleton
class DatabaseProvider @Inject() (config: Config) extends Provider[Database] {
  lazy val get = Database.forConfig("myapp.database", config)
}

/** Closes database connections safely.  Important on dev restart. */
class UserDAOCloseHook @Inject()(dao: UserDAO, lifecycle: ApplicationLifecycle) {
  lifecycle.addStopHook { () =>
    Future.successful(dao.close())
  }
}
