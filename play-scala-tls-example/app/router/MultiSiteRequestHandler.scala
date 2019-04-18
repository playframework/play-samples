package router

import javax.inject._
import play.api.ApplicationLoader.DevContext
import play.api.OptionalDevContext
import play.api.mvc.RequestHeader
import play.api.http.HttpConfiguration
import play.api.http.DefaultHttpRequestHandler
import play.api.http.HttpErrorHandler
import play.api.http._
import play.core.WebCommands

class MultiSiteRequestHandler @Inject() (webCommands: WebCommands,
                                         optDevContext: OptionalDevContext,
                                         errorHandler: HttpErrorHandler,
                                         configuration: HttpConfiguration,
                                         filters: HttpFilters,
                                         defaultRouter: router.Routes,
                                         oneRouter: one.Routes,
                                         twoRouter: two.Routes)
  extends DefaultHttpRequestHandler(
    webCommands,
    optDevContext.devContext,
    defaultRouter,
    errorHandler,
    configuration,
    filters.filters) {

  override def routeRequest(request: RequestHeader) = {

    // assuming local ports in development -- will need to change in prod
    val host = request.host.split(":")(0)

    host match {
      case "one.example.com" =>
        oneRouter.routes.lift(request)

      case "two.example.com" =>
        twoRouter.routes.lift(request)

      case other =>
        super.routeRequest(request)
    }

  }
}
