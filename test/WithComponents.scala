import play.api.ApplicationLoader.Context
import play.api.{ApplicationLoader, BuiltInComponents, Environment, Mode}

trait WithComponents[T <: BuiltInComponents] {
  def createComponents(context: Context): T
}

trait WithContext {

  def context: ApplicationLoader.Context = {
    val classLoader = ApplicationLoader.getClass.getClassLoader
    val env = new Environment(new java.io.File("."), classLoader, Mode.Test)
    ApplicationLoader.createContext(env)
  }

}
