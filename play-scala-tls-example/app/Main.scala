import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import play.api.libs.ws._
import com.typesafe.config.ConfigFactory
import play.api.libs.ws.ahc.{AhcWSClient, AhcWSClientConfigFactory}

import scala.util.{Failure, Success}

/**
 * Connects to example.com with a WS client running from Main.
 *
 * Please see http://www.playframework.com/documentation/latest/WsSSL for more details.
 */
object Main {

  private val logger = org.slf4j.LoggerFactory.getLogger("application")

  def printResponse(response:WSResponse) = {
    response.headers.foreach { header =>
      logger.info(s"header = $header")
    }
    val body = response.body
    logger.info(s"body = $body")
  }

  def main(args: Array[String]) {
    import scala.concurrent.ExecutionContext.Implicits.global

    val config = AhcWSClientConfigFactory.forConfig(ConfigFactory.load("ws.conf"), getClass.getClassLoader)
    val name = "testing"
    val system = ActorSystem(name)
    implicit val materializer = ActorMaterializer(namePrefix = Some(name))(system)

    val client = AhcWSClient(config)
    val futureResponse = client.url("https://one.example.com:9443").get()
    futureResponse.onComplete {
      case Success(response) =>
        printResponse(response)
        client.close() // closing the client must be done manually.

      case Failure(f) =>
        logger.error(s"failure = $f", f)
        client.close()
    }

    system.terminate()
  }
}
