package controllers;

import javax.inject.Inject;

import play.routing.*;

import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

public class HomeController extends Controller {

  public Result index(final Http.Request request) {
    return ok(views.html.index.render(request));
  }

  public Result javascriptRoutes(final Http.Request request) {
    return ok(
      JavaScriptReverseRouter.create(
        "jsRoutes",
        "jQuery.ajax",
        request.host(),
        routes.javascript.JavaEventSourceController.streamClock()
      )
    ).as("text/javascript");
  }
}
