import org.openqa.selenium.WebDriver
import play.api.test._
import play.api.{ApplicationLoader, Environment, Mode}

class WithGreetingApplication extends WithApplicationLoader(new GreetingApplicationLoader)
class WithGreetingApplicationBrowser[W <: WebDriver]
  extends WithBrowser[W](app = new GreetingApplicationLoader().load(ApplicationLoader.createContext(new Environment(new java.io.File("."), ApplicationLoader.getClass.getClassLoader, Mode.Test))))
