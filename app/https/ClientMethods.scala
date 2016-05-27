package https

import akka.stream.Materializer
import org.asynchttpclient.AsyncHttpClientConfig
import play.api.Environment
import play.api.libs.ws.WSConfigParser
import play.api.libs.ws.ahc._

/**
 *
 */
trait ClientMethods {

  def createClient(configuration: play.api.Configuration, environment: Environment)(implicit mat: Materializer) = {
    val parser = new WSConfigParser(configuration, environment)
    val clientConfig = parser.parse()
    val ahcParser = new AhcWSClientConfigParser(clientConfig, configuration, environment)
    val ahcConfig = ahcParser.parse()
    val builder = new AhcConfigBuilder(ahcConfig)
    val asyncHttpClientConfig: AsyncHttpClientConfig = builder.build()
    val client = new AhcWSClient(asyncHttpClientConfig)(mat)
    client
  }
}
