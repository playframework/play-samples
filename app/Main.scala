import play.api.{Mode, Environment}
import play.api.libs.ws._
import play.api.libs.ws.ning._
import play.api.libs.ws.ssl.debug.DebugConfiguration

import com.typesafe.config.ConfigFactory

import scala.util.{Failure, Success}

/**
 * Connects to example.com with a WS client running from Main.
 *
 * Please see http://www.playframework.com/documentation/2.3.x/WsSSL for more details.
 */
object Main {

  def newClient(configuration: play.api.Configuration, environment: Environment): NingWSClient = {
    val parser = new WSConfigParser(configuration, environment)
    val clientConfig = parser.parse()
    val ningParser = new NingWSClientConfigParser(clientConfig, configuration, environment)
    val ningClientConfig = ningParser.parse()
    val builder = new NingAsyncHttpClientConfigBuilder(ningClientConfig)
    val asyncHttpClientConfig = builder.build()
    val client = new NingWSClient(asyncHttpClientConfig)
    client
  }

  def printResponse(response:WSResponse) = {
    response.allHeaders.foreach { header =>
      Console.println(s"header = $header")
    }
    val body = response.body
    Console.println(s"body = $body")
  }

  def main(args: Array[String]) {
    import scala.concurrent.ExecutionContext.Implicits.global

    val config = play.api.Configuration(ConfigFactory.load("ws.conf"))
    val environment = play.api.Environment.simple(new java.io.File("./conf"), Mode.Dev)
    val client = newClient(config, environment)

    val futureResponse = client.url("https://example.com:9443").get()
    futureResponse.onComplete {
      case Success(response) =>
        printResponse(response)
        client.close() // closing the client must be done manually.

      case Failure(f) =>
        Console.println(s"failure = $f")
        client.close()
    }
  }
}
