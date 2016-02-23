/*
 * Copyright (C) 2009-2016 Typesafe Inc. <http://www.typesafe.com>
 */
 package controllers;

import akka.NotUsed;
import akka.stream.javadsl.Source;
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
        final Source<String, NotUsed> source = getSource();
        return ok().chunked(source.via(Comet.flow("parent.clockChanged"))).as(Http.MimeTypes.HTML);
    }

}
