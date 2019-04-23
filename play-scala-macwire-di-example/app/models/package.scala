import play.api.libs.json.Json

package object models {

  case class Greeting(id: Int = -1, message: String, name: String)

  object Greeting {
    implicit val GreetingFormat = Json.format[Greeting]
  }

}
