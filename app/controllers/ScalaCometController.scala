/*
 * Copyright (C) 2009-2016 Typesafe Inc. <http://www.typesafe.com>
 */
package controllers

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.{Inject, Singleton}

import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import play.api.http.ContentTypes
import play.api.libs.Comet
import play.api.libs.streams.Streams
import play.api.mvc.{Controller, _}

import scala.concurrent.duration._

@Singleton
class ScalaCometController @Inject() (materializer: Materializer) extends Controller with ScalaTicker {

  def index() = Action {
    Ok(views.html.scalacomet())
  }

  def enumeratorClock() = Action {
    implicit val m = materializer
    Results.Ok.chunked(enumerator &> Comet("parent.clockChanged")).as(ContentTypes.HTML)
  }

  def streamClock() = Action {
    Ok.chunked(source via Comet.flow("parent.clockChanged", None)).as(ContentTypes.HTML)
  }
}
