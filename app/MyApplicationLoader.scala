import play.api._
import play.api.libs.ws.ahc._
import play.api.mvc._
import play.api.routing.Router

class MyApplicationLoader extends ApplicationLoader {
  var components: MyComponents = _

  def load(context: ApplicationLoader.Context): Application = {
    LoggerConfigurator(context.environment.classLoader).foreach {
      _.configure(context.environment)
    }

    components = new MyComponents(context)
    components.application
  }
}

class MyComponents(context: ApplicationLoader.Context) 
  extends BuiltInComponentsFromContext(context)
  with AhcWSComponents
  with _root_.controllers.AssetsComponents {

  lazy val parsers: PlayBodyParsers = playBodyParsers

  lazy val actionBuilder: ActionBuilder[Request, AnyContent] = defaultActionBuilder

  lazy val controllerComponents: ControllerComponents = DefaultControllerComponents(
    defaultActionBuilder, playBodyParsers, messagesApi, langs, fileMimeTypes, executionContext
  )

  lazy val homeController = new _root_.controllers.HomeController(controllerComponents)

  lazy val router: Router = new _root_.router.Routes(httpErrorHandler, homeController, assets)
}
