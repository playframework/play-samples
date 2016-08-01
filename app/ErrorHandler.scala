import javax.inject.{Inject, Provider, Singleton}

import play.api._
import play.api.http.Status._
import play.api.http.{ContentTypes, DefaultHttpErrorHandler, HttpErrorHandlerExceptions}
import play.api.mvc.Results._
import play.api.mvc._
import play.api.routing.Router
import play.core.SourceMapper

import scala.concurrent._
import scala.util.control.NonFatal

/**
 * Provides a stripped down error handler that does not use HTML in error pages.
 *
 * https://www.playframework.com/documentation/2.5.x/ScalaErrorHandling
 */
@Singleton
class ErrorHandler(environment: Environment,
                   configuration: Configuration,
                   sourceMapper: Option[SourceMapper] = None,
                   optionRouter: => Option[Router] = None)
  extends DefaultHttpErrorHandler(environment, configuration, sourceMapper, optionRouter) with AcceptExtractors with Rendering {

  private val logger = org.slf4j.LoggerFactory.getLogger("application.ErrorHandler")

  // This maps through Guice so that the above constructor...
  @Inject
  def this(environment: Environment,
           configuration: Configuration,
           sourceMapper: OptionalSourceMapper,
           router: Provider[Router]) = {
    this(environment, configuration, sourceMapper.sourceMapper, Some(router.get))
  }

  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    logger.debug(s"onClientError: statusCode = $statusCode, uri = ${request.uri}, message = $message")

    Future.successful {
      val result = statusCode match {
        case BAD_REQUEST =>
          Results.BadRequest(message)
        case FORBIDDEN =>
          Results.Forbidden(message)
        case NOT_FOUND =>
          Results.NotFound(message)
        case clientError if statusCode >= 400 && statusCode < 500 =>
          Results.Status(statusCode)
        case nonClientError =>
          val msg = s"onClientError invoked with non client error status code $statusCode: $message"
          throw new IllegalArgumentException(msg)
      }
      result
    }
  }

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    try {
      val usefulException = HttpErrorHandlerExceptions.throwableToUsefulException(sourceMapper,
        environment.mode == Mode.Prod, exception)

      logger.error(
        s"! @${usefulException.id} - Internal server error, for (${request.method}) [${request.uri}] ->",
        usefulException
      )

      Future.successful {
        InternalServerError
      }
    } catch {
      case NonFatal(e) =>
        logger.error("Error while handling error", e)
        Future.successful(InternalServerError)
    }
  }
}
