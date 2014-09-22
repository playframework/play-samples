import scala.reflect.ClassTag

import com.softwaremill.macwire.Wired

import controllers.Assets
import play.api.ApplicationLoader.Context
import play.api._
import play.api.i18n._
import play.api.inject.{BindingKey, Injector}
import play.core.DefaultWebCommands

/**
 * Application loader that wires up the application dependencies using Macwire
 */
class GreetingApplicationLoader extends ApplicationLoader {
  def load(context: Context): Application = {
    (new BuiltInComponentsFromContext(context) with GreetingComponents).application
  }
  override def createInjector(env: Environment, conf: Configuration, modules: Seq[Any]) = {
    // Hack so tests will work
    Some(new GreetingComponents {
      def sourceMapper = new OptionalSourceMapper(None)
      def environment = env
      def configuration = conf
      def webCommands = new DefaultWebCommands
    }.injector)
  }
}

trait GreetingComponents extends BuiltInComponents with GreetingModule
  with I18nComponents {

  import com.softwaremill.macwire.MacwireMacros._

  lazy val routes: Routes = wire[Routes]

  override lazy val global = Global
  // required by FakeApplication
  lazy val globalPlugin = new GlobalPlugin(application)

  lazy val assets: Assets = new Assets

  override lazy val injector: Injector = new WiredInjector(wiredInModule(this))
}

class WiredInjector(wired: Wired) extends Injector {
  def instanceOf[T](clazz: Class[T]) = wired.lookupSingleOrThrow(clazz)
  def instanceOf[T](key: BindingKey[T]) = instanceOf(key.getClass).asInstanceOf[T]
  def instanceOf[T](implicit ct: ClassTag[T]) = instanceOf(ct.runtimeClass).asInstanceOf[T]
}
