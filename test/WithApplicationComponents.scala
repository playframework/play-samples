import play.api.ApplicationLoader.Context
import play.api._

trait WithApplicationComponents[T <: BuiltInComponents] {
  private var _components: T = _

  // accessed to get the components in tests
  final def components: T = _components

  // overridden by subclasses
  def createComponents(context: Context): T

  // creates a new application and sets the components
  def newApplication: Application = {
    _components = createComponents(context)
    _components.application
  }

  def context: ApplicationLoader.Context = {
    val classLoader = ApplicationLoader.getClass.getClassLoader
    val env = new Environment(new java.io.File("."), classLoader, Mode.Test)
    ApplicationLoader.createContext(env)
  }
}
