package filters

import javax.inject.Inject

import controllers.routes
import play.api.mvc.{EssentialAction, EssentialFilter, RequestHeader}

import scala.concurrent.ExecutionContext

/**
 * Set up a more flexible content security policy that points to self and the given
 * websocket URL.
 */
class ContentSecurityPolicyFilter @Inject()(implicit ec: ExecutionContext) extends EssentialFilter {

  override def apply(next: EssentialAction): EssentialAction = EssentialAction { request: RequestHeader =>
    val webSocketUrl = routes.HomeController.chat().webSocketURL()(request)
    next(request).map { result =>
      result.withHeaders("Content-Security-Policy" -> s"connect-src 'self' $webSocketUrl")
    }
  }
}
