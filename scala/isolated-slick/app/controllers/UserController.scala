package controllers

import com.example.user.{User, UserDAO}
import models.UserRequest
import play.api.i18n.{Lang, Messages}
import play.api.libs.json.Json
import play.api.mvc._

import java.time.Instant
import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class UserController @Inject() (userDAO: UserDAO, cc: ControllerComponents)(
    implicit ec: ExecutionContext
) extends AbstractController(cc) {
  /**
   * An implicit for default Languages for the Inputs to default to english.
   */
  implicit val defaultLanguage: Messages = cc.messagesApi.preferred(Seq(Lang("en")))

  /**
   * GET - Get the List of all the users from the userDao
   * @return Html View of Index with all the users
   */
  def index: Action[AnyContent] = Action.async { implicit request =>
    userDAO.all.map { users =>
      Ok(views.html.index(users))
    }
  }

  /**
   * GET - Find all Users  and return them on a String array ids.
   * @return List[String] of all the user ids.
   */
  def findAll: Action[AnyContent] = Action.async{implicit request =>
    userDAO.all.map{ users=>
      Ok(Json.toJson(users.map(_.id)))
    }
  }

  /**
   * GET - Gets the Create page loading to create a new User
   * @return The Html Page for the Create page
   */
  def create: Action[AnyContent] = Action {
    val emptyForm = UserRequest.form
    Ok(views.html.create(emptyForm))
  }


  /**
   * POST - Create a new user from the UserRequest.form validated
   * @return Redirect into the index view to see the new user
   */
  def save: Action[AnyContent] = Action { implicit request =>
    UserRequest.form
      .bindFromRequest()
      .fold(
        formWithErrors => BadRequest(views.html.create(formWithErrors)),
        formData => {
          userDAO.create(
            User(
              UUID.randomUUID().toString,
              formData.email,
              createdAt = Instant.now(),
              updatedAt = Option(Instant.now())
            )
          )
          Redirect(routes.UserController.index)
        }
      )
  }

  /**
   * GET - Opens the Edit Page for the new user with the form filled from userDAO.lookup
   * @param id The Id Parameter of the edited user.
   * @return The Html page of the edit filled with the user email to be edited.
   */
  def edit(id: String): Action[AnyContent] = Action.async { implicit request =>
    userDAO.lookup(id).map { userData =>
      userData.fold(
        Redirect(routes.UserController.index)
      ) { user =>
        val filledForm = UserRequest.form
          .fill(UserRequest(id = Option(user.id), email = user.email))
        Ok(views.html.update(filledForm))
      }
    }
  }

  /**
   * POST - Update the user via the validated form to the id requested if that exists.
   * @param id The id parameter of the user to edit
   * @return Redirect to html page of index with the updated user on it.
   */
  def update(id: String): Action[AnyContent] = Action.async {
    implicit request =>
      userDAO.lookup(id).map { userData =>
        userData.fold(
          Redirect(routes.UserController.index)
        ) { user =>
          UserRequest.form
            .bindFromRequest()
            .fold(
              formWithErrors => BadRequest(views.html.create(formWithErrors)),
              formData => {
                userDAO.update(
                  User(
                    id,
                    formData.email,
                    user.createdAt,
                    updatedAt = Option(Instant.now())
                  )
                )
                Redirect(routes.UserController.index)
              }
            )
        }
      }
  }

  /**
   * GET - Delete a user from the database based on id - Done GET due to href link
   * @param id The user to delete
   * @return Redirects to index page where the new user doesnt exist.
   */
  def delete(id: String): Action[AnyContent] = Action.async {
    implicit unused =>
      userDAO.lookup(id).map { userData =>
        userData.fold(
          Redirect(routes.UserController.index)
        ) { _ =>
          userDAO.delete(id)
          Redirect(routes.UserController.index)
        }
      }
  }
}
