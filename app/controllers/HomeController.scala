/*
 * Copyright (C) 2009-2016 Lightbend Inc. <http://www.lightbend.com>
 */
package controllers

import javax.inject.Inject

import play.api.mvc._
import play.api.routing._

class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def index() = Action {
    Ok(views.html.index())
  }

  def javascriptRoutes = Action { implicit request =>
    Ok(
      JavaScriptReverseRouter("jsRoutes")(
        routes.javascript.JavaEventSourceController.streamClock
      )
    ).as("text/javascript")
  }
}
