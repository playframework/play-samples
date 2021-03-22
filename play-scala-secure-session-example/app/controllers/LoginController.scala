package controllers

import javax.inject.{ Inject, Singleton }

import play.api.data.Form
import play.api.mvc._

import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class LoginController @Inject() (
  userAction: UserInfoAction,
  sessionGenerator: SessionGenerator,
  cc: ControllerComponents
)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {

  def login = userAction.async { implicit request: UserRequest[AnyContent] =>
    val successFunc = { userInfo: UserInfo =>
      sessionGenerator.createSession(userInfo).map {
        case (sessionId, encryptedCookie) =>
          val session = request.session + (SESSION_ID -> sessionId)
          Redirect(routes.HomeController.index)
            .withSession(session)
            .withCookies(encryptedCookie)
      }
    }

    val errorFunc = { badForm: Form[UserInfo] =>
      Future.successful {
        BadRequest(views.html.index(badForm)).flashing(FLASH_ERROR -> "Could not login!")
      }
    }

    form.bindFromRequest().fold(errorFunc, successFunc)
  }

}
