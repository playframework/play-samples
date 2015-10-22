package com.example.user

import javax.inject._
import com.google.inject.AbstractModule
import com.typesafe.config.Config

import scala.concurrent.Future

/**
 *
 */
class SlickUserModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[slick.jdbc.JdbcBackend.Database]).toProvider(classOf[DatabaseProvider])
    bind(classOf[UserDAO]).to(classOf[SlickUserDAO])
  }
}


@Singleton
class DatabaseProvider @Inject() (config: Config) extends Provider[slick.jdbc.JdbcBackend.Database] {

  private val db = slick.jdbc.JdbcBackend.Database.forConfig("myapp.database", config)

  override def get(): slick.jdbc.JdbcBackend.Database = db
}



