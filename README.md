# Play Framework with Compile Time DI Tests

This is an example of Play 2.5.x using the Scala API with manually wired compile time dependency injection.

The application loader here is `MyApplicationLoader` which uses `MyComponents` to wire together an injector.  In testing, there are some places where a `WSClient` has to be used, and so some additional components have to be added in.
 
To do this, the injector has to be implemented specifying all of the built in components, plus the WS API, which is made available through `AhcWSComponents`:


```
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

  override lazy val injector =  new SimpleInjector(NewInstanceInjector) + router + cookieSigner + csrfTokenSigner + httpConfiguration + tempFileCreator + global + crypto + wsApi + messagesApi

  lazy val router: Router = new Routes(httpErrorHandler, homeController, assets)

  lazy val homeController = new controllers.HomeController()
  lazy val assets = new controllers.Assets(httpErrorHandler)
}
```

Now that `MyComponents` has the `AhcWSComponents` trait, it can use `components.wsClient` anywhere.  It's most convenient to define the components in a trait for testing:  

```
trait CompileTimeComponents {

  lazy val components = {
    val classLoader = ApplicationLoader.getClass.getClassLoader
    val env = new Environment(new java.io.File("."), classLoader, Mode.Test)
    val context = ApplicationLoader.createContext(env)
    new MyComponents(context)
  }

}
```

Once the `CompileTimeComponents` is defined, then a specification can use `components.application` to expose the application to ScalaTest and call out to the client:

```
class ServerSpec extends PlaySpec
  with Results
  with CompileTimeComponents
  with OneServerPerSuite
  with ScalaFutures {

  override implicit lazy val app: Application = components.application

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

Please refer to https://www.playframework.com/documentation/2.5.x/ScalaCompileTimeDependencyInjection and https://www.playframework.com/documentation/2.5.x/ScalaTestingWithScalaTest#Using-ScalaTest-+-Play for documentation.
