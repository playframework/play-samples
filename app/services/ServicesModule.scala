package services

trait ServicesModule {

  import com.softwaremill.macwire.MacwireMacros._

  lazy val greetingService = wire[GreetingService]

}
