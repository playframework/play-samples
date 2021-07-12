import play.api.libs.json.{Json, OFormat}

package object models {

  case class Greeting(id: Int = -1, message: String, name: String)

  object Greeting {
    implicit val GreetingFormat: OFormat[Greeting] = Json.format[Greeting]
  }

}
