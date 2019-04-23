import org.scalatestplus.play.FakeApplicationFactory
import play.api.inject.DefaultApplicationLifecycle
import play.api.{Application, ApplicationLoader, Configuration, Environment}
import play.core.DefaultWebCommands

trait GreeterApplicationFactory extends FakeApplicationFactory {

  private class GreetingApplicationBuilder {
    def build(): Application = {
      val env = Environment.simple()
      val context = ApplicationLoader.Context(
        environment = env,
        sourceMapper = None,
        webCommands = new DefaultWebCommands(),
        initialConfiguration = Configuration.load(env),
        lifecycle = new DefaultApplicationLifecycle()
      )
      val loader = new GreetingApplicationLoader()
      loader.load(context)
    }
  }

  def fakeApplication(): Application = new GreetingApplicationBuilder().build()

}
