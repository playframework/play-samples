package controllers

import org.apache.pekko.stream.Materializer
import play.api.http.ContentTypes
import play.api.libs.Comet
import play.api.mvc._
import views.html.helper.CSPNonce

import javax.inject.{Inject, Singleton}

@Singleton
class ScalaCometController @Inject()(cc: ControllerComponents, materializer: Materializer) extends AbstractController(cc)
  with ScalaTicker {

  def index(): Action[AnyContent] = Action { implicit request =>
    Ok(views.html.scalacomet())
  }

  def streamClock(): Action[AnyContent] = Action { implicit request =>
    Ok.chunked(stringSource.via(Comet.string("parent.clockChanged", CSPNonce()))).as(ContentTypes.HTML)
  }
}
