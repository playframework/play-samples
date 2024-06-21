package controllers

import javax.inject.{ Inject, Singleton }

import play.api.mvc._
import services.session.SessionService

@Singleton
class LogoutController @Inject() (
  sessionService: SessionService,
  cc: ControllerComponents
) extends AbstractController(cc) {

  def logout = Action { implicit request: Request[AnyContent] =>
    // When we delete the session id, removing the session id is enough to render the
    // user info cookie unusable.
    request.session.get(SESSION_ID).foreach { sessionId =>
      sessionService.delete(sessionId)
    }

    discardingSession {
      Redirect(routes.HomeController.index)
    }
  }

}
