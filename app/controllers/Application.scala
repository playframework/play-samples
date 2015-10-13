package controllers

import java.util.UUID
import javax.inject.Inject

import akka.actor.ActorSystem
import com.example.user.{User, UserDAO}
import play.api._
import play.api.mvc._

class Application @Inject() (userDAO: UserDAO, actorSystem:ActorSystem) extends Controller {

  private val ec = actorSystem.dispatchers.lookup("myapp.database-dispatcher")

  def index = Action.async {
    // Set up an execution context from the akka dispatchers library...

    val id = UUID.randomUUID.toString
    userDAO.create(User(id, "some@example.com")).map { rows =>
      Ok(views.html.index(s"Connected to database and created user $id"))
    }(ec)
  }

}
