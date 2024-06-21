package services

class GreetingService {

  def greetingMessage(language: String) = language match {
    case "it" => "Messi"
    case _ => "Hello"
  }

}
