package com.example.user.slick

import com.github.tminglei.slickpg._
import play.api.libs.json.{JsValue, Json}

/**
 * A postgresql driver with extended Joda and JSON support.
 */
trait MyPostgresDriver extends ExPostgresDriver
with PgArraySupport
with PgDateSupportJoda
with PgPlayJsonSupport {

  object MyAPI extends API with DateTimeImplicits with JsonImplicits {
    implicit val strListTypeMapper = new SimpleArrayJdbcType[String]("text").to(_.toList)
    implicit val playJsonArrayTypeMapper =
      new AdvancedArrayJdbcType[JsValue](pgjson,
        (s) => utils.SimpleArrayUtils.fromString[JsValue](Json.parse(_))(s).orNull,
        (v) => utils.SimpleArrayUtils.mkString[JsValue](_.toString())(v)
      ).to(_.toList)
  }

  // jsonb support is in postgres 9.4.0 onward; for 9.3.x use "json"
  def pgjson = "jsonb"

  override val api = MyAPI
}

object MyPostgresDriver extends MyPostgresDriver

