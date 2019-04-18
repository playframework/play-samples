package controllers

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import akka.stream.scaladsl.Source
import play.api.libs.json._

import scala.concurrent.duration._

trait ScalaTicker {

  def stringSource: Source[String, _] = {
    val df: DateTimeFormatter = DateTimeFormatter.ofPattern("HH mm ss")
    val tickSource = Source.tick(0.millis, 100.millis, "TICK")
    val s = tickSource.map(_ => df.format(ZonedDateTime.now()))
    s
  }

  def jsonSource: Source[JsValue, _] = {
    val tickSource = Source.tick(0.millis, 100.millis, "TICK")
    val s = tickSource.map(_ => Json.toJson(ZonedDateTime.now))
    s
  }

}
