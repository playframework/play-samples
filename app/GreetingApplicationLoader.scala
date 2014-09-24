

import controllers.Assets
import play.api.ApplicationLoader.Context
import play.api._
import play.api.i18n._
import play.api.inject.SimpleInjector
import play.core.DefaultWebCommands

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
    Some(new SimpleInjector(components.injector) +
      new GlobalPlugin(components.application) +
      components.injector +
      components.routes)
  }
}

trait GreetingComponents extends BuiltInComponents with GreetingModule with I18nComponents {
  import com.softwaremill.macwire.MacwireMacros._

  override lazy val global = Global
  lazy val assets = new Assets
  lazy val routes = wire[Routes]
}
