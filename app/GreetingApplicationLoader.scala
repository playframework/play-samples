

import controllers.Assets
import play.api.ApplicationLoader.Context
import play.api._
import play.api.http._
import play.api.i18n._
import play.api.inject.{Injector, NewInstanceInjector, SimpleInjector}
import play.core.{DefaultWebCommands, Router}

/**
 * Application loader that wires up the application dependencies using Macwire
 */
class GreetingApplicationLoader extends ApplicationLoader {

  def load(context: Context): Application = {
    (new BuiltInComponentsFromContext(context) with GreetingComponents).application
  }

  override def createInjector(env: Environment, conf: Configuration, modules: Seq[Any]) = {
    // FIXME: FakeApplication uses the runtime injector so we have to hack around it for now
    val context = new Context(env, None, new DefaultWebCommands, conf)
    val components = new BuiltInComponentsFromContext(context) with GreetingComponents
    Some(new SimpleInjector(components.injector) + new GlobalPlugin(components.application))
  }
}

trait GreetingComponents extends BuiltInComponents with GreetingModule with I18nComponents {
  import com.softwaremill.macwire.MacwireMacros._

  lazy val assets = wire[Assets]
  lazy val errorHandler: HttpErrorHandler =
    new DefaultHttpErrorHandler(environment, configuration, sourceMapper, Some(routes))
  lazy val routes: Router.Routes = wire[Routes]

  lazy val filters: HttpFilters = NoHttpFilters

  // FIXME: should not need to override the injector here
  // Seems like in tests we're getting instances of GlobalSettings and JavaCompatibleHttpRequestHandler
  override lazy val injector: Injector = {
    new SimpleInjector(NewInstanceInjector) + routes + crypto + httpConfiguration + (DefaultGlobal: GlobalSettings)
  }
}
