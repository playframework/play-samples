/*
 * Copyright (C) 2009-2016 Typesafe Inc. <http://www.typesafe.com>
 */
package controllers

import play.api.mvc.{Action, Controller}

class HomeController extends Controller {

  def index() = Action {
    Ok(views.html.index())
  }

}
