package com.example.user.slick

import javax.inject.{Inject, Singleton}

import org.joda.time.DateTime
import slick.driver.JdbcProfile
import slick.jdbc.JdbcBackend.Database
import com.example.user._

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions

/**
 * A User DAO implemented with Slick, leveraging Slick code gen.
 *
 * Note that you must run "flyway/flywayMigrate" before "compile" here.
 *
 * @param db the slick database that this user DAO is using internally, bound through Module.
 * @param ec a CPU bound execution context.  Slick manages blocking JDBC calls with its
 *    own internal thread pool, so Play's default execution context is fine here.
 */
@Singleton
class SlickUserDAO @Inject()(db: Database)(implicit ec: ExecutionContext) extends UserDAO with Tables {

  override val profile: JdbcProfile = _root_.slick.driver.H2Driver

  import profile.api._

  private val queryById = Compiled(
    (id: Rep[String]) => Users.filter(_.id === id))

  def lookup(id: String): Future[Option[User]] = {
    val f: Future[Option[UsersRow]] = db.run(queryById(id).result.headOption)
    f.map(maybeRow => maybeRow.map(usersRowToUser))
  }

  def all: Future[Seq[User]] = {
    val f = db.run(Users.result)
    f.map(seq => seq.map(usersRowToUser))
  }

  def update(user: User): Future[Int] = {
    db.run(queryById(user.id).update(userToUsersRow(user)))
  }

  def delete(id: String): Future[Int] = {
    db.run(queryById(id).delete)
  }

  def create(user: User): Future[Int] = {
    db.run(
      Users += userToUsersRow(user.copy(createdAt = DateTime.now()))
    )
  }

  def close(): Future[Unit] = {
    Future.successful(db.close())
  }

  private def userToUsersRow(user: User): UsersRow = {
    UsersRow(user.id, user.email, user.createdAt, user.updatedAt)
  }

  private def usersRowToUser(usersRow: UsersRow): User = {
    User(usersRow.id, usersRow.email, usersRow.createdAt, usersRow.updatedAt)
  }
}
