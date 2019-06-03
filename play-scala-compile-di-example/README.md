# play-scala-compile-di-example

This is an example of Play using the Scala API with manually wired compile time dependency injection.

The application loader here is `MyApplicationLoader` which uses `MyComponents` to wire together an injector.

For testing, a `MyApplicationFactory` is defined and mixed in:

```scala
trait MyApplicationFactory extends FakeApplicationFactory {

  override def fakeApplication: Application = {
    val env = Environment.simple(new File("."))
    val configuration = Configuration.load(env)
    val context = ApplicationLoader.Context(
      environment = env,
      sourceMapper = None,
      webCommands = new DefaultWebCommands(),
      initialConfiguration = configuration,
      lifecycle = new DefaultApplicationLifecycle()
    )
    val loader = new MyApplicationLoader()
    loader.load(context)
  }

}
```

Once the `MyApplicationFactory` is defined, the fake application is used by TestSuite types:

```scala
class ServerSpec extends PlaySpec
  with BaseOneServerPerSuite
  with MyApplicationFactory
  with ScalaFutures
  with IntegrationPatience {

  private implicit val implicitPort = port

  "Server query should" should {
    "work" in {
      whenReady(play.api.test.WsTestClient.wsUrl("/").get) { response =>
        response.status mustBe play.api.http.Status.OK
      }
    }
  }
}
```

## Further Documentation

* [Compile Time Dependency Injection](https://www.playframework.com/documentation/latest/ScalaCompileTimeDependencyInjection)
* [Using ScalaTest + Play](https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest#Using-ScalaTest-+-Play)
* [ScalaTest User Guide](http://www.scalatest.org/user_guide)
* [ScalaTest/Scalactic 3.0.0 Release Notes](http://www.scalatest.org/release_notes/3.0.0)
* [ScalaTest Plus Play](https://github.com/playframework/scalatestplus-play)
