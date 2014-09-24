import controllers.GreeterController
import play.api.i18n.Langs
import services.ServicesModule

trait GreetingModule extends ServicesModule {

  import com.softwaremill.macwire.MacwireMacros._

  lazy val greeterController = wire[GreeterController]

  def langs: Langs
}
