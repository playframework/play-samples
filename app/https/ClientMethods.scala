package https

import play.api.Environment
import play.api.libs.ws.WSConfigParser
import play.api.libs.ws.ning.{NingAsyncHttpClientConfigBuilder, NingWSClient, NingWSClientConfigParser}

/**
 *
 */
trait ClientMethods {

  def createClient(configuration: play.api.Configuration, environment: Environment): NingWSClient = {
    val parser = new WSConfigParser(configuration, environment)
    val clientConfig = parser.parse()
    val ningParser = new NingWSClientConfigParser(clientConfig, configuration, environment)
    val ningClientConfig = ningParser.parse()
    val builder = new NingAsyncHttpClientConfigBuilder(ningClientConfig)
    val asyncHttpClientConfig = builder.build()
    val client = new NingWSClient(asyncHttpClientConfig)
    client
  }
}
