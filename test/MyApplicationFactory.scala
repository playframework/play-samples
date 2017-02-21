import org.scalatestplus.play.FakeApplicationFactory
import play.api._
import play.api.i18n._
import play.api.inject._
import play.core.DefaultWebCommands

trait MyApplicationFactory extends FakeApplicationFactory {

  /**
   * An application builder for running an application in tests
   */
  class MyApplicationBuilder {

    def build(): Application = {
      val env = Environment.simple()
      val context = ApplicationLoader.Context(
        environment = env,
        sourceMapper = None,
        webCommands = new DefaultWebCommands(),
        initialConfiguration = Configuration.load(env),
        lifecycle = new DefaultApplicationLifecycle()
      )
      val loader = new MyApplicationLoader()
      loader.load(context)
    }
  }

  override def fakeApplication(): Application = {
    new MyApplicationBuilder().build()
  }
}
