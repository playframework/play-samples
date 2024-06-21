package models

import play.api.data.Forms._
import play.api.data.Form

/**
 * A UserRequest model to get the form to update/create users.
 * @param id The Optional User Id to counter add/edit actions
 * @param email The Email to be registered/edited
 */
case class UserRequest(id: Option[String], email: String)

/**
 * The Companion object of the form
 */
object UserRequest {
  val form: Form[UserRequest] = Form(
    mapping(
      "id" -> optional(nonEmptyText),
      "email" -> email
    )(UserRequest.apply)(UserRequest.unapply)
  )
}