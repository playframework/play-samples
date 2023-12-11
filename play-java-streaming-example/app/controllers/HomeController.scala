package controllers

import javax.inject.Inject

import play.api.mvc._
import play.api.routing._

class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def index(): Action[AnyContent] = Action { implicit request =>
    Ok(views.html.index())
  }

  def javascriptRoutes: Action[AnyContent] = Action { implicit request =>
    Ok(
      JavaScriptReverseRouter("jsRoutes")(
        routes.javascript.JavaEventSourceController.streamClock
      )
    ).as("text/javascript")
  }
}
