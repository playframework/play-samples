package controllers

import javax.inject.{Inject, Singleton}

import play.api.http.ContentTypes
import play.api.libs.EventSource
import play.api.mvc._

@Singleton
class ScalaEventSourceController @Inject()(cc: ControllerComponents) extends AbstractController(cc) with ScalaTicker {

  def index(): Action[AnyContent] = Action { implicit request =>
    Ok(views.html.scalaeventsource())
  }

  def streamClock(): Action[AnyContent] = Action { implicit request =>
    Ok.chunked(stringSource via EventSource.flow).as(ContentTypes.EVENT_STREAM)
  }

}
