import controllers.GreeterController
import play.api.Application
import services.ServicesModule

trait GreetingModule extends ServicesModule {

  import com.softwaremill.macwire.MacwireMacros._

  lazy val greeterController = wire[GreeterController]
}
