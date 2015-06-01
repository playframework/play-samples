package controllers

import java.util.UUID
import javax.inject.Inject

import akka.actor.ActorSystem
import models.User
import play.api._
import play.api.mvc._
import slick.UserDAO

class Application @Inject() (userDAO: UserDAO, actorSystem:ActorSystem) extends Controller {

  // Set up an execution context from the akka dispatchers library...
  private implicit val ec = actorSystem.dispatchers.lookup("myapp.dispatcher")

  def index = Action.async {
    userDAO.create(User(UUID.randomUUID.toString, "some@example.com")).map { rows =>
      Ok(views.html.index(s"row = ${rows}"))
    }
  }

}
