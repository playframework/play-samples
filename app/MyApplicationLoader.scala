import play.api._
import play.api.i18n._
import play.api.inject._
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.routing.Router
import router.Routes

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
  with I18nComponents
  with AhcWSComponents {

  override lazy val injector =  new SimpleInjector(NewInstanceInjector) + router + cookieSigner + csrfTokenSigner + httpConfiguration + tempFileCreator + global + crypto + wsApi + messagesApi

  lazy val router: Router = new Routes(httpErrorHandler, homeController, assets)

  lazy val homeController = new controllers.HomeController()
  lazy val assets = new controllers.Assets(httpErrorHandler)
}
