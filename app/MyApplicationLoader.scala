import play.api._
import play.api.inject._
import play.api.libs.ws.ahc._
import play.api.mvc._
import play.api.routing.Router

class MyApplicationLoader extends ApplicationLoader {
  def load(context: ApplicationLoader.Context) = {
    LoggerConfigurator(context.environment.classLoader).foreach {
      _.configure(context.environment)
    }
    new MyComponents(context).application
  }
}

class MyComponents(context: ApplicationLoader.Context) 
  extends BuiltInComponentsFromContext(context)
  with AhcWSComponents
  with play.filters.HttpFiltersComponents
  with _root_.controllers.AssetsComponents
  with ControllerComponents {

  lazy val parsers: PlayBodyParsers = playBodyParsers

  lazy val actionBuilder: ActionBuilder[Request, AnyContent] = defaultActionBuilder

  lazy val homeController = new _root_.controllers.HomeController(this)

  lazy val router: Router = new _root_.router.Routes(httpErrorHandler, homeController, assets)

}
