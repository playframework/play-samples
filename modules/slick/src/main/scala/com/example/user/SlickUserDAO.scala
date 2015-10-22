package com.example.user

import javax.inject.{Inject, Singleton}

import scala.concurrent.Future

import slick.jdbc.JdbcBackend.Database

/**
 *
 */
@Singleton
class SlickUserDAO @Inject() (db:Database) extends UserDAO {
  import MyPostgresDriver.api._

  private val users = TableQuery[Users]

  private val queryById = Compiled(
    (id: Rep[String]) => users.filter(_.id === id))


  def lookup(id: String): Future[Option[User]] = {
    db.run(queryById(id).result.headOption)
  }

  def all: Future[Seq[User]] = {
    db.run(users.result)
  }

  def update(user:User) = {
    db.run(queryById(user.id).update(user))
  }

  def delete(id:String) = {
    db.run(queryById(id).delete)
  }

  def create(user:User): Future[Int] = {
    db.run(
      users += user
    )
  }

  def close(): Unit = {
    db.close()
  }

  class Users(tag: Tag) extends Table[User](tag, "USERS") {
    def id = column[String]("ID", O.PrimaryKey)
    def email = column[String]("EMAIL")

    def * = (id, email) <> (User.tupled, User.unapply)
  }
}
