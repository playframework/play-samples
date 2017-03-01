package integration

import org.scalatestplus.play._
import play.api.test.{Helpers, TestServer}
import play.api.{Application, Mode}
import org.scalatest._
import org.scalatestplus.play.guice.GuiceFakeApplicationFactory
import play.core.server.{AkkaHttpServer, ServerConfig, ServerProvider}

/**
 * Runs a server test using the SSL port as the default
 */
trait GuiceOneHttpsServerPerTest extends TestSuiteMixin with ServerProvider with GuiceFakeApplicationFactory { this: TestSuite =>

  private var privateApp: Application = _

  /**
   * Implicit method that returns the `Application` instance for the current test.
   */
  implicit final def app: Application = synchronized { privateApp }

  /**
   * Creates new instance of `Application` with parameters set to their defaults. Override this method if you
   * need an `Application` created with non-default parameter values.
   */
  def newAppForTest(testData: TestData): Application = fakeApplication()

  /**
   * The port used by the `TestServer`.  By default this will be set to the result returned from
   * `Helpers.testServerPort`. You can override this to provide a different port number.
   */
  lazy val port: Int = Helpers.testServerPort

  implicit val portNumber: PortNumber = PortNumber(port)

  /**
   * Creates new `Application` and running `TestServer` instances before executing each test, and
   * ensures they are cleaned up after the test completes. You can access the `Application` from
   * your tests as `app` and the `TestServer`'s port number as `port`.
   *
   * @param test the no-arg test function to run with a fixture
   * @return the `Outcome` of the test execution
   */
  abstract override def withFixture(test: NoArgTest) = {
    synchronized { privateApp = newAppForTest(test) }

    val testServer = new TestServer(
      ServerConfig(port = None, sslPort = Some(port), mode = Mode.Test, rootDir = app.path),
      app,
      None
    )

    Helpers.running(testServer) {
      super.withFixture(test)
    }
  }

  def createServer(context: ServerProvider.Context) =
    new AkkaHttpServer(context.config, context.appProvider, context.actorSystem, context.materializer,
      context.stopHook)

}
