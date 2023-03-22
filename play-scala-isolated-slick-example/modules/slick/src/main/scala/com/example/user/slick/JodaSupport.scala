package com.example.user.slick

import org.joda.time.DateTime
import slick.jdbc._
import com.github.tototoshi.slick._

trait JodaSupport {
  private val driver: JdbcProfile = _root_.slick.jdbc.H2Profile

  private val dateTimeMapperDelegate: JodaDateTimeMapper = new JodaDateTimeMapper(driver)
  implicit val datetimeTypeMapper: JdbcProfile#DriverJdbcType[DateTime] = dateTimeMapperDelegate.TypeMapper
  implicit val getDatetimeResult: GetResult[DateTime] = dateTimeMapperDelegate.JodaGetResult.getResult
  implicit val getDatetimeOptionResult: GetResult[Option[DateTime]] = dateTimeMapperDelegate.JodaGetResult.getOptionResult
  implicit val setDatetimeParameter: SetParameter[DateTime] = dateTimeMapperDelegate.JodaSetParameter.setJodaParameter
  implicit val setDatetimeOptionParameter: SetParameter[Option[DateTime]] = dateTimeMapperDelegate.JodaSetParameter.setJodaOptionParameter
}