import javax.inject.{Inject, Provider, Singleton}

import akka.actor.ActorSystem
import com.example.user.slick.SlickUserDAO
import com.example.user.{UserDAO, UserDAOExecutionContext}
import com.google.inject.AbstractModule
import com.typesafe.config.Config
import play.api.inject.ApplicationLifecycle
import play.api.libs.concurrent.CustomExecutionContext
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
    bind(classOf[UserDAOExecutionContext]).to(classOf[SlickUserDAOExecutionContext])
    bind(classOf[UserDAOCloseHook]).asEagerSingleton()
  }
}

@Singleton
class DatabaseProvider @Inject() (config: Config) extends Provider[Database] {
  lazy val get = Database.forConfig("myapp.database", config)
}

// Use a custom execution context
// https://www.playframework.com/documentation/latest/ScalaAsync#Creating-non-blocking-actions
class SlickUserDAOExecutionContext @Inject()(actorSystem: ActorSystem)
  extends CustomExecutionContext(actorSystem, "myapp.database-dispatcher")
    with UserDAOExecutionContext

/** Closes database connections safely.  Important on dev restart. */
class UserDAOCloseHook @Inject()(dao: UserDAO, lifecycle: ApplicationLifecycle) {
  lifecycle.addStopHook { () =>
    Future.successful(dao.close())
  }
}
