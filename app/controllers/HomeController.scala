package controllers

import play.api._
import play.api.mvc._

class HomeController extends Controller {

  private val allowedHosts = Seq("one", "two")

  def index = Action { implicit request =>
    Ok(views.html.index())
  }

  def forHost(host: String) = Action { implicit request =>
    if (allowedHosts.contains(host)) {
      Ok(views.html.forHost(host))
    } else {
      Forbidden("Not an allowed host")
    }
  }

}

