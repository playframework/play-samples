package controllers

import javax.inject.Inject

import models.Widget
import play.api.data._
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}

/**
 * The classic WidgetController using I18nSupport.
 *
 * I18nSupport provides implicits that create a Messages instances from
 * a request using implicit conversion.
 */
class WidgetController @Inject()(cc: ControllerComponents) extends AbstractController(cc) with I18nSupport {
  import WidgetForm._

  private val widgets = scala.collection.mutable.ArrayBuffer(
    Widget("Widget 1", 123),
    Widget("Widget 2", 456),
    Widget("Widget 3", 789)
  )
  private val postUrl = routes.WidgetController.createWidget()

  def index = Action {
    Ok(views.html.index())
  }

  def listWidgets = Action { implicit request: Request[AnyContent] =>
    // Pass an unpopulated form to the template
    Ok(views.html.listWidgets(widgets, widgetForm, postUrl))
  }

  // This will be the action that handles our form post
  def createWidget = Action { implicit request: Request[AnyContent] =>
    val errorFunction = { formWithErrors: Form[Widget] =>
      // This is the bad case, where the form had validation errors.
      // Let's show the user the form again, with the errors highlighted.
      // Note how we pass the form with errors to the template.
      BadRequest(views.html.listWidgets(widgets, formWithErrors, postUrl))
    }

    val successFunction = { widget: Widget =>
      // This is the good case, where the form was successfully parsed as a Widget.
      widgets.append(widget)
      Redirect(routes.WidgetController.listWidgets())
    }

    val formValidationResult = widgetForm.bindFromRequest
    formValidationResult.fold(errorFunction, successFunction)
  }

}