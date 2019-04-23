/*
 * Copyright (C) 2009-2016 Lightbend Inc. <http://www.lightbend.com>
 */
 package controllers;

import play.libs.Comet;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Singleton;

@Singleton
public class JavaCometController extends Controller implements JavaTicker {

    public Result index() {
        return ok(views.html.javacomet.render());
    }

    public Result streamClock() {
        return ok().chunked(getStringSource().via(Comet.string("parent.clockChanged"))).as(Http.MimeTypes.HTML);
    }

    public Result jsonClock() {
        return ok().chunked(getJsonSource().via(Comet.json("parent.clockChanged"))).as(Http.MimeTypes.HTML);
    }

}
