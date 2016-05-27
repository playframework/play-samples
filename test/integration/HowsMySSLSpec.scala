package integration

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import org.junit.runner._
import org.specs2.runner._
import play.api.Mode
import play.api.libs.json.{JsValue, Json}
import play.api.test._

import scala.concurrent.duration._

@RunWith(classOf[JUnitRunner])
class HowsMySSLSpec extends PlaySpecification with https.ClientMethods {

  "WS" should {

    "connect to a remote server " in {
      val input = """play.ws.ssl {
                    |  //enabledProtocols = [ TLSv1.2 ]
                    |}
                  """.stripMargin
      val config = play.api.Configuration(ConfigFactory.parseString(input).withFallback(ConfigFactory.defaultReference()))
      val environment = play.api.Environment.simple(new java.io.File("./conf"), Mode.Dev)

      val name = "testing"
      val system = ActorSystem(name)
      implicit val materializer = ActorMaterializer(namePrefix = Some(name))(system)

      val client = createClient(config, environment)

      val response = await(client.url("https://www.howsmyssl.com/a/check").get())(2 seconds)
      val jsonOutput = response.json

      system.terminate()
      client.close()

      val tlsVersion = (jsonOutput \ "tls_version").as[String]
      tlsVersion must contain("TLS 1.2")
    }
  }

}
