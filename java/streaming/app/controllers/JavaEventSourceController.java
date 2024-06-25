package controllers;

import org.apache.pekko.stream.javadsl.Source;
import play.libs.EventSource;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Singleton;

@Singleton
public class JavaEventSourceController extends Controller implements JavaTicker {

    public Result index(final Http.Request request) {
        return ok(views.html.javaeventsource.render(request));
    }

    public Result streamClock() {
        final Source<EventSource.Event, ?> eventSource = getStringSource().map(EventSource.Event::event);
        return ok().chunked(eventSource.via(EventSource.flow())).as(Http.MimeTypes.EVENT_STREAM);
    }

}
