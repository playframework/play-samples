import org.scalatestplus.play.FakeApplicationFactory
import play.api._
import play.api.inject._
import play.api.libs.ws.WSClient
import play.core.DefaultWebCommands

trait MyApplicationFactory extends FakeApplicationFactory {

  private val loader = new MyApplicationLoader()

  override def fakeApplication: Application = {
    val env = Environment.simple()
    val context = ApplicationLoader.Context(
      environment = env,
      sourceMapper = None,
      webCommands = new DefaultWebCommands(),
      initialConfiguration = Configuration.load(env),
      lifecycle = new DefaultApplicationLifecycle()
    )
    loader.load(context)
  }

  implicit def wsClient: WSClient = loader.components.wsClient
}
