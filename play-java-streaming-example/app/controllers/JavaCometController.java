package controllers;

import play.libs.Comet;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.helper.CSPNonce;

import javax.inject.Singleton;

@Singleton
public class JavaCometController extends Controller implements JavaTicker {

    public Result index(final Http.Request request) {
        return ok(views.html.javacomet.render(request));
    }

    public Result streamClock(final Http.Request request) {
        return ok().chunked(getStringSource().via(Comet.string("parent.clockChanged", CSPNonce.apply(request.asScala())))).as(Http.MimeTypes.HTML);
    }

    public Result jsonClock(final Http.Request request) {
        return ok().chunked(getJsonSource().via(Comet.json("parent.clockChanged", CSPNonce.apply(request.asScala())))).as(Http.MimeTypes.HTML);
    }

}
