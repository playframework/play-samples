import scala.reflect.ClassTag

import com.softwaremill.macwire.Wired

import controllers.Assets
import play.api.ApplicationLoader.Context
import play.api.i18n._
import play.api.inject.{BindingKey, Injector}
import play.api.{Application, BuiltInComponentsFromContext, ApplicationLoader}

/**
 * Application loader that wires up the application dependencies using Macwire
 */
class GreetingApplicationLoader extends ApplicationLoader {
  def load(context: Context): Application = {
    (new BuiltInComponentsFromContext(context) with GreetingComponents).application
  }
}

trait GreetingComponents extends BuiltInComponentsFromContext
  with GreetingModule
  with I18nComponents {

  import com.softwaremill.macwire.MacwireMacros._

  override lazy val global = Global
  lazy val assets: Assets = new Assets
  lazy val routes: Routes = {
    val prefix = "/"
    wire[Routes]
  }

  override lazy val injector: Injector = new WiredInjector(wiredInModule(this))
}

class WiredInjector(wired: Wired) extends Injector {
  def instanceOf[T](clazz: Class[T]) = wired.lookupSingleOrThrow(clazz)
  def instanceOf[T](key: BindingKey[T]) = instanceOf(key.getClass).asInstanceOf[T]
  def instanceOf[T](implicit ct: ClassTag[T]) = instanceOf(ct.runtimeClass).asInstanceOf[T]
}
