package com.example.user

import org.joda.time.DateTime

import scala.concurrent.{ExecutionContext, Future}

/**
 * An implementation dependent DAO.  This could be implemented by Slick, Cassandra, or a REST API.
 */
trait UserDAO {

  def lookup(id: String): Future[Option[User]]

  def all: Future[Seq[User]]

  def update(user: User): Future[Int]

  def delete(id: String): Future[Int]

  def create(user: User): Future[Int]

  def close(): Future[Unit]
}

/**
 * Implementation independent aggregate root.
 */
case class User(id: String, email: String, createdAt: DateTime, updatedAt: Option[DateTime])

/**
 * Type safe execution context for operations on UserDAO.
 */
trait UserDAOExecutionContext extends ExecutionContext
