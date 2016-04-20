import play.api.{ApplicationLoader, Environment, Mode}

/**
 * Exposes the components in the application loader to the various specs.
 */
trait CompileTimeComponents {

  lazy val components = {
    val classLoader = ApplicationLoader.getClass.getClassLoader
    val env = new Environment(new java.io.File("."), classLoader, Mode.Test)
    val context = ApplicationLoader.createContext(env)
    new MyComponents(context)
  }

}
