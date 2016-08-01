package filters

import javax.inject._

import akka.stream.Materializer
import com.google.inject.AbstractModule
import com.netaporter.uri.Uri
import com.typesafe.config.Config
import play.api.Configuration
import play.api.mvc.{Filter, RequestHeader, Result, Results}

import scala.concurrent.{ExecutionContext, Future}

/**
 * Sends a Strict Transport Security Filter header to clients over HTTPS.
 *
 * https://tools.ietf.org/html/rfc6797
 */
class StrictTransportSecurityFilter @Inject()(config: StrictTransportSecurityConfig)
                                             (implicit val mat: Materializer, ec: ExecutionContext)
  extends Filter {

  def apply(nextFilter: RequestHeader => Future[Result])(request: RequestHeader): Future[Result] = {
    if (request.secure) {
      nextFilter(request).map { result =>
        result.withHeaders(hstsHeader)
      }
    } else {
      Future.successful {
        val secureURL = generateSecureURL(request)
        // Use a permanent redirect to keep people on HTTPS
        Results.PermanentRedirect(secureURL)
      }
    }
  }

  def hstsHeader: (String, String) = {
    ("Strict-Transport-Security", s"""max-age=${config.maxAge.getSeconds}""")
  }

  def generateSecureURL(request: RequestHeader) = {
    import com.netaporter.uri.dsl._
    val uri: Uri = request.uri
    val secureUri = uri
      .withScheme("https")
      .withHost(config.secureHost)
      .withPort(config.securePort)
    secureUri.toString()
  }

}

/**
 * Configuration DTO for the HSTS filter
 *
 * @param maxAge how long the HSTS header max-age should be
 * @param secureHost the secure hostname
 * @param securePort the secure port
 */
final case class StrictTransportSecurityConfig(maxAge: java.time.Duration,
                                               secureHost: String,
                                               securePort: Int)

object StrictTransportSecurityConfig {
  def fromConfiguration(config: Config): StrictTransportSecurityConfig = {
    val hstsConfig = config.getConfig("restapi.filters.hsts")

    val maxAge = hstsConfig.getDuration("maxAge")
    val secureHost = hstsConfig.getString("secureHost")
    val securePort = hstsConfig.getInt("securePort")

    StrictTransportSecurityConfig(maxAge, secureHost, securePort)
  }
}

/**
 * Pulls in setting for StrictTransportSecurityFilter via conf/application.conf
 *
 * @param configuration the application.conf configuration
 */
@Singleton
class StrictTransportSecurityConfigProvider @Inject()(configuration: Configuration)
  extends Provider[StrictTransportSecurityConfig] {

  lazy val get: StrictTransportSecurityConfig = {
    StrictTransportSecurityConfig.fromConfiguration(configuration.underlying)
  }
}
