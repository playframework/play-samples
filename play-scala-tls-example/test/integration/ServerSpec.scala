package integration

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play._
import play.api.libs.ws.ahc.{AhcWSClient, AhcWSClientConfigFactory}

/**
 * Test the server comes up with given settings
 */
class ServerSpec extends PlaySpec with GuiceOneHttpsServerPerTest with ScalaFutures with BeforeAndAfterAll {

  val name = "testing"
  val system = ActorSystem(name)
  implicit val materializer = ActorMaterializer(namePrefix = Some(name))(system)

  val config = ConfigFactory.load("ws").withFallback(ConfigFactory.defaultReference())
  val wsConfig = AhcWSClientConfigFactory.forConfig(config)
  val client = AhcWSClient(wsConfig)

  "Server" should {
    "work fine over https" in pendingUntilFixed {
      whenReady(client.url(s"https://example.com:$port/").get()) { result =>
        result.body must include("This is the page")
      }
    }
  }

  override protected def afterAll(): Unit = {
    client.close()
    system.terminate()
  }
}
