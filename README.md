# Play Framework with Compile Time DI Tests

This is an example of Play using the Scala API with manually wired compile time dependency injection.

The application loader here is `MyApplicationLoader` which uses `MyComponents` to wire together an injector.  In testing, there are some places where a `WSClient` has to be used, and so some additional components have to be added in.
 
To do this, the injector has to be implemented specifying all of the built in components, plus the WS API, which is made available through `AhcWSComponents`:

``` scala
import play.api._
import play.api.i18n._
import play.api.inject._
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.routing.Router
import router.Routes

class MyApplicationLoader extends ApplicationLoader {
  def load(context: ApplicationLoader.Context) = {
    LoggerConfigurator(context.environment.classLoader).foreach {
      _.configure(context.environment)
    }
    new MyComponents(context).application
  }
}

class MyComponents(context: ApplicationLoader.Context) 
  extends BuiltInComponentsFromContext(context)
  with I18nComponents
  with AhcWSComponents {

  override lazy val injector =  {
    new SimpleInjector(NewInstanceInjector) +
      router +
      cookieSigner +
      csrfTokenSigner +
      httpConfiguration +
      tempFileCreator +
      global +
      crypto +
      wsApi +
      messagesApi
  }

  lazy val router: Router = new Routes(httpErrorHandler, homeController, assets)

  lazy val homeController = new controllers.HomeController()
  lazy val assets = new controllers.Assets(httpErrorHandler)
}
```

Now that `MyComponents` has the `AhcWSComponents` trait, it can use `components.wsClient` anywhere.  

The ScalaTest suite mixins such as `OneAppPerSuite` use `GuiceApplicationLoader` for all the implicit Application set up, so to use dependency injection, the trait must be extended to use the components using types:

``` scala
trait OneServerPerSuiteWithComponents[T <: BuiltInComponents]
  extends OneServerPerSuite
    with WithApplicationComponents[T] {
  this: Suite =>

  override implicit lazy val app: Application = newApplication
}
```

where `WithApplicationComponents` is defined as:

``` scala
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
```

Then, depending on your components, you can set up a subtype of `OneServerPerSuiteWithComponents` using `MyComponents`:

``` scala
trait OneServerPerSuiteWithMyComponents
  extends OneServerPerSuiteWithComponents[MyComponents] {
  this: Suite =>

  override def createComponents(context: Context): MyComponents = new MyComponents(context)
}
```

Once the `OneServerPerSuiteWithMyComponents` is defined, you must call out to the `app` lazy val, after which you can access the `components` field to get at the wsClient:

``` scala
class ServerSpec extends PlaySpec
  with OneServerPerSuiteWithMyComponents
  with Results
  with ScalaFutures {

  "Server query should" should {

    "work" in {
      implicit val ec = app.materializer.executionContext
      val wsClient = components.wsClient

      whenReady(wsUrl("/")(portNumber, wsClient).get) { response =>
        response.status mustBe OK
      }
    }

  }
}
```

## Further Documentation

* https://www.playframework.com/documentation/2.5.x/ScalaCompileTimeDependencyInjection 
* https://www.playframework.com/documentation/2.5.x/ScalaTestingWithScalaTest#Using-ScalaTest-+-Play 
* http://www.scalatest.org/user_guide
* http://www.scalatest.org/release_notes/3.0.0
* https://github.com/playframework/scalatestplus-play
