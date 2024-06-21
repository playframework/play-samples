package views.tags.forms

import views.html.tags.forms.field_constructor

object FormHelper {
  import views.html.helper.FieldConstructor
  implicit val inlineBootstrapConstructor: FieldConstructor = FieldConstructor(field_constructor.f)

  val classRegex = "(?s)(<(?:input|textarea|select)[^>]*\\sclass=[\"'])".r
  val noClassRegex = "(?s)(<(?:input|textarea|select))((?:(?!\\sclass=\").+)>)".r

  def addClassValue(text: String, classValue: String) = {
    val str = classRegex.replaceFirstIn(text, s"$$1$classValue ")
    noClassRegex.replaceFirstIn(str, s"""$$1 class="$classValue"$$2""")
  }
}

