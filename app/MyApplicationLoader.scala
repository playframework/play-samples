import play.api._
import play.api.inject._
import play.api.libs.ws.ahc._
import play.api.mvc._
import play.api.routing.Router
import play.core.DefaultWebCommands

class MyApplicationLoader extends ApplicationLoader {
  def load(context: ApplicationLoader.Context) = {
    LoggerConfigurator(context.environment.classLoader).foreach {
      _.configure(context.environment)
    }
    new MyComponents(context).application
  }
}

/**
 * An application builder for running an application in tests
 */
class MyApplicationBuilder {

  def build(): Application = {
    val env = Environment.simple()
    val context = ApplicationLoader.Context(
      environment = env,
      sourceMapper = None,
      webCommands = new DefaultWebCommands(),
      initialConfiguration = Configuration.load(env),
      lifecycle = new DefaultApplicationLifecycle()
    )
    val loader = new MyApplicationLoader()
    loader.load(context)
  }
}

class MyComponents(context: ApplicationLoader.Context) 
  extends BuiltInComponentsFromContext(context)
  with AhcWSComponents
  with _root_.controllers.AssetsComponents
  with ControllerComponents {

  lazy val parsers: PlayBodyParsers = PlayBodyParsers(httpConfiguration.parser, httpErrorHandler, materializer, tempFileCreator)

  lazy val actionBuilder: ActionBuilder[Request, AnyContent] = DefaultActionBuilder(parsers.anyContent)

  override lazy val injector =  {
    new SimpleInjector(NewInstanceInjector) +
      router +
      cookieSigner +
      csrfTokenSigner +
      httpConfiguration +
      tempFileCreator +
      wsClient +
      messagesApi
  }

  lazy val homeController = new _root_.controllers.HomeController(this)

  lazy val router: Router = new _root_.router.Routes(httpErrorHandler, homeController, assets)

}
