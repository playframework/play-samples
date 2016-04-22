/*
 * Copyright (C) 2009-2016 Lightbend Inc. <http://www.lightbend.com>
 */
package controllers

import javax.inject.{Inject, Singleton}

import akka.stream.Materializer
import play.api.http.ContentTypes
import play.api.libs.Comet
import play.api.mvc.{Controller, _}

@Singleton
class ScalaCometController @Inject() (materializer: Materializer) extends Controller with ScalaTicker {

  def index() = Action {
    Ok(views.html.scalacomet())
  }

  // Show deprecated methods here
  def enumeratorClock() = Action {
    implicit val m = materializer
    Results.Ok.chunked(enumerator &> Comet("parent.clockChanged"))
  }

  def streamClock() = Action {
    Ok.chunked(stringSource via Comet.string("parent.clockChanged")).as(ContentTypes.HTML)
  }
}
