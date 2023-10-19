package integration

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.Materializer
import com.typesafe.config.ConfigFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.PatienceConfiguration
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.Seconds
import org.scalatest.time.Span
import org.scalatestplus.play._
import play.api.libs.ws.WSResponse
import play.api.libs.ws.ahc.AhcWSClient
import play.api.libs.ws.ahc.AhcWSClientConfigFactory
import play.api.libs.ws.WSBodyReadables.readableAsString

import scala.concurrent.Future

/**
 * Test the server comes up with given settings
 */
class ServerSpec extends PlaySpec with GuiceOneHttpsServerPerTest with ScalaFutures with BeforeAndAfterAll {

  val name = "testing"
  val system = ActorSystem(name)
  implicit val materializer: Materializer = Materializer.matFromSystem(system)

  val config = ConfigFactory.load("ws").withFallback(ConfigFactory.defaultReference())
  val wsConfig = AhcWSClientConfigFactory.forConfig(config)
  val client = AhcWSClient(wsConfig)

  "Server" should {
    "work fine over https" in {
      val eventualResponse: Future[WSResponse] =
        client
          .url(s"https://localhost:$httpsPort/")
          .withVirtualHost("example.com")
          .get()
      httpsPort mustEqual(19001)
      val timeout: PatienceConfiguration.Timeout = Timeout(Span(30, Seconds))
      whenReady(eventualResponse, timeout) { result =>
        result.body must include("This is the page")
      }
    }
  }

  override protected def afterAll(): Unit = {
    client.close()
    system.terminate()
  }
}
