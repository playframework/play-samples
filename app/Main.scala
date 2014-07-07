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

  def newClient(rawConfig: play.api.Configuration): NingWSClient = {
    val classLoader = Thread.currentThread().getContextClassLoader
    val parser = new DefaultWSConfigParser(rawConfig, classLoader)
    val clientConfig = parser.parse()
    clientConfig.ssl.map {
      _.debug.map(new DebugConfiguration().configure)
    }
    val builder = new NingAsyncHttpClientConfigBuilder(clientConfig)
    val client = new NingWSClient(builder.build())
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
    val client = newClient(config)

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
