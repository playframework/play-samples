package com.example.user

import scala.concurrent.Future

/**
 *
 */
trait UserDAO {

  def lookup(id: String): Future[Option[User]]

  def all: Future[Seq[User]]

  def update(user:User)

  def delete(id:String)

  def create(user:User): Future[Int]

  def close()
}

case class User(id:String, email:String)
