package controllers

import javax.inject.{Inject, Singleton}

import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}
import services.session.SessionService
import services.user.{UserInfo, UserInfoService}


@Singleton
class LoginController @Inject()(sessionService: SessionService,
                                userInfoService: UserInfoService,
                                factory: UserInfoCookieBakerFactory,
                                cc: ControllerComponents) extends AbstractController(cc) {

  def login = Action { implicit request: Request[AnyContent] =>
    def successFunc = { userInfo: UserInfo =>
      val secretKey = userInfoService.newSecretKey
      val sessionId = sessionService.create(secretKey)

      // Session id and user info are distinct cookies.  The user info lives as long as you
      // have a secret key for it.  The session dies when the browser closes or you logout.
      val cookieBaker = factory.createCookieBaker(secretKey)
      val userInfoCookie = cookieBaker.encodeAsCookie(Some(userInfo))
      val session = request.session + ("sessionId" -> sessionId)

      play.api.Logger.info("Created a new username " + userInfo)

      Redirect(routes.HomeController.index()).withSession(session).withCookies(userInfoCookie)
    }

    UserInfoForm.form.bindFromRequest().fold({ form =>
      play.api.Logger.error("could not log in!")
      Redirect(routes.HomeController.index()).flashing("error" -> "Could not login!")
    }, successFunc)
  }

}
