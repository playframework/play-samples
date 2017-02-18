
import _root_.controllers.AssetsComponents
import com.softwaremill.macwire._
import play.api.ApplicationLoader.Context
import play.api._
import play.api.i18n._
import play.api.mvc._
import play.api.routing.Router
import router.Routes

/**
 * Application loader that wires up the application dependencies using Macwire
 */
class GreetingApplicationLoader extends ApplicationLoader {
  def load(context: Context): Application = new GreetingComponents(context).application
}

class GreetingComponents(context: Context) extends BuiltInComponentsFromContext(context)
  with GreetingModule
  with ControllerComponents
  with AssetsComponents
  with I18nComponents {

  // set up logger
  LoggerConfigurator(context.environment.classLoader).foreach {
    _.configure(context.environment)
  }

  lazy val parsers: PlayBodyParsers = playBodyParsers
  lazy val actionBuilder: ActionBuilder[Request, AnyContent] = defaultActionBuilder
  lazy val controllerComponents: ControllerComponents = this

  lazy val router: Router = {
    // add the prefix string in local scope for the Routes constructor
    val prefix: String = "/"
    wire[Routes]
  }
}
