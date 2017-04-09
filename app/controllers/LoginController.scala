package controllers

import javax.inject.{Inject, Singleton}

import play.api.data.Form
import play.api.mvc._

@Singleton
class LoginController @Inject()(action: UserInfoAction,
                                sessionGenerator: SessionGenerator,
                                cc: ControllerComponents) extends AbstractController(cc) {

  def login = action { implicit request: UserRequest[AnyContent] =>
    val successFunc = { userInfo: UserInfo =>
      val (sessionId, encryptedCookie) = sessionGenerator.createSession(userInfo)
      val session = request.session + (SESSION_ID -> sessionId)
      Redirect(routes.HomeController.index())
        .withSession(session)
        .withCookies(encryptedCookie)
    }

    val errorFunc = { badForm: Form[UserInfo] =>
      BadRequest(views.html.index(badForm)).flashing(FLASH_ERROR -> "Could not login!")
    }

    form.bindFromRequest().fold(errorFunc, successFunc)
  }

}
