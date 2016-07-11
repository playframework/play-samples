package controllers

import play.api._
import play.api.mvc._

class HomeController extends Controller {

  def index = Action { implicit request =>
    val filtered = request.host.split(":")(0).replace(".example.com", "")

    Redirect(routes.HomeController.forHost(filtered))
  }

  def forHost(host: String) = Action { implicit request =>
    // prevent any browser caching...
    Ok(views.html.forHost(host)).withHeaders("Cache-Control" -> "no-store")
  }

}

