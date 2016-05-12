/*
 * Copyright (C) 2009-2016 Lightbend Inc. <http://www.lightbend.com>
 */
package controllers

import play.api.mvc.{Action, Controller}

class HomeController extends Controller {

  def index() = Action {
    Ok(views.html.index())
  }

}
