package modules

import javax.inject.{Inject, Singleton, Provider}

import com.google.inject.AbstractModule
import com.typesafe.config.Config
import play.api.{Environment, Configuration}
import play.api.inject.ApplicationLifecycle

import scala.concurrent.Future

/**
 *
 */
class DatabaseModule(environment: Environment,
                     configuration: Configuration) extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[Config]).toInstance(configuration.underlying)
    bind(classOf[slick.jdbc.JdbcBackend.Database]).toProvider(classOf[DatabaseProvider])
    bind(classOf[slick.UserDAO]).asEagerSingleton()
  }
}

@Singleton
class DatabaseProvider @Inject() (config: Config, lifecycle: ApplicationLifecycle) extends Provider[slick.jdbc.JdbcBackend.Database] {

  private val db = slick.jdbc.JdbcBackend.Database.forConfig("myapp.database", config)

  lifecycle.addStopHook { () =>
    Future.successful {
      db.close()
    }
  }

  override def get(): slick.jdbc.JdbcBackend.Database = db
}
