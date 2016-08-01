import javax.inject.Inject

import play.api.http._
import play.api.mvc._
import play.api.routing.Router

/**
 * Handles all requests.
 */
class RequestHandler @Inject()(router: Router,
                               errorHandler: HttpErrorHandler,
                               configuration: HttpConfiguration,
                               filters: HttpFilters)
  extends DefaultHttpRequestHandler(router, errorHandler, configuration, filters) {

  override def handlerForRequest(request: RequestHeader): (RequestHeader, Handler) = {
    super.handlerForRequest {
      // ensures that REST API does not need a trailing "/"
      if (request.uri.startsWith("/posts")) {
        addTrailingSlash(request)
      } else {
        request
      }
    }
  }

  private def addTrailingSlash(origReq: RequestHeader): RequestHeader = {
    if (! origReq.path.endsWith("/")) {
      val path = origReq.path + "/"
      if (origReq.rawQueryString.isEmpty) {
        origReq.copy(path = path, uri = path)
      } else {
        origReq.copy(path = path, uri = path + s"?${origReq.rawQueryString}")
      }
    } else {
      origReq
    }
  }
}
