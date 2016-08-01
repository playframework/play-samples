package controllers

import play.api.mvc.{Action, Controller}

class HomeController extends Controller {

  def index = Action { implicit request =>
    Ok(views.html.index())
  }

}
