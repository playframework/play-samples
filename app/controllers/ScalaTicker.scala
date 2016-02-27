/*
 * Copyright (C) 2009-2016 Typesafe Inc. <http://www.typesafe.com>
 */
package controllers

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import akka.actor.Cancellable
import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import play.api.libs.json._
import play.api.libs.streams.Streams

import scala.concurrent.duration._

trait ScalaTicker {

  def enumerator(implicit materializer: Materializer) = {
    val publisher = stringSource.runWith(Sink.asPublisher(fanout = false))
    val enumerator = Streams.publisherToEnumerator(publisher)
    enumerator
  }

  def stringSource: Source[String, _] = {
    val df: DateTimeFormatter = DateTimeFormatter.ofPattern("HH mm ss")
    val tickSource = Source.tick(0 millis, 100 millis, "TICK")
    val s = tickSource.map((tick) => df.format(ZonedDateTime.now()))
    s
  }

  def jsonSource: Source[JsValue, _] = {
    val tickSource = Source.tick(0 millis, 100 millis, "TICK")
    val s = tickSource.map((tick) => Json.toJson(ZonedDateTime.now))
    s
  }

}
