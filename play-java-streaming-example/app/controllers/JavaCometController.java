package controllers;

import play.libs.Comet;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Singleton;

@Singleton
public class JavaCometController extends Controller implements JavaTicker {

    public Result index(Http.Request request) {
        return ok(views.html.javacomet.render(request.asScala()));
    }

    public Result streamClock() {
        return ok().chunked(getStringSource().via(Comet.string("parent.clockChanged"))).as(Http.MimeTypes.HTML);
    }

    public Result jsonClock() {
        return ok().chunked(getJsonSource().via(Comet.json("parent.clockChanged"))).as(Http.MimeTypes.HTML);
    }

}
