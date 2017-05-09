package controllers

import models.Widget

object WidgetForm {
  import play.api.data.Forms._
  import play.api.data.Form

  /**
   * The form definition for the "create a widget" form.
   * It specifies the form fields and their types,
   * as well as how to convert from a Widget to form data and vice versa.
   */
  val widgetForm = Form(
    mapping(
      "name" -> text,
      "price" -> number
    )(Widget.apply)(Widget.unapply)
  )

}
