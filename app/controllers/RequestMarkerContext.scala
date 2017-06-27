package controllers

import play.api.MarkerContext
import play.api.mvc._

import scala.language.implicitConversions

/**
 * Provide host and path logging on the request, available in application.json
 */
trait RequestMarkerContext {

  implicit def requestHeaderToMarkerContext(implicit request: RequestHeader): MarkerContext = {
    import net.logstash.logback.marker.LogstashMarker
    import net.logstash.logback.marker.Markers._

    val requestMarkers: LogstashMarker = append("host", request.host)
      .and(append("path", request.path))

    MarkerContext(requestMarkers)
  }

}