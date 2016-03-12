package controllers

import java.util.UUID
import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import com.example.user.{User, UserDAO, UserDAOExecutionContext}
import play.api.mvc._

@Singleton
class HomeController @Inject() (userDAO: UserDAO, userDAOExecutionContext: UserDAOExecutionContext) extends Controller {

  private val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)

  implicit val ec = userDAOExecutionContext

  def index = Action.async {
    logger.info("Calling index")
    userDAO.all.map { users =>
      logger.info(s"Calling index: users = ${users}")
      Ok(views.html.index(users))
    }
  }

}
