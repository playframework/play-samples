package controllers;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.LoggingAdapter;
import akka.japi.Pair;
import akka.japi.pf.PFBuilder;
import akka.stream.Materializer;
import akka.stream.javadsl.*;
import play.libs.F;
import play.mvc.*;

import akka.event.Logging;

import javax.inject.Inject;
import java.net.URI;
import java.util.concurrent.CompletableFuture;

import org.webjars.play.WebJarsUtil;

/**
 * A very simple chat client using websockets.
 */
public class HomeController extends Controller {

    private final Flow<String, String, NotUsed> userFlow;
    private final WebJarsUtil webJarsUtil;


    @Inject
    public HomeController(ActorSystem actorSystem,
                          Materializer mat,
                          WebJarsUtil webJarsUtil) {
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
        LoggingAdapter logging = Logging.getLogger(actorSystem.eventStream(), logger.getName());

        //noinspection unchecked
        Source<String, Sink<String, NotUsed>> source = MergeHub.of(String.class)
                .log("source", logging)
                .recoverWithRetries(-1, new PFBuilder().match(Throwable.class, e -> Source.empty()).build());
        Sink<String, Source<String, NotUsed>> sink = BroadcastHub.of(String.class);

        Pair<Sink<String, NotUsed>, Source<String, NotUsed>> sinkSourcePair = source.toMat(sink, Keep.both()).run(mat);
        Sink<String, NotUsed> chatSink = sinkSourcePair.first();
        Source<String, NotUsed> chatSource = sinkSourcePair.second();
        this.userFlow = Flow.fromSinkAndSource(chatSink, chatSource).log("userFlow", logging);

        this.webJarsUtil = webJarsUtil;
    }

    public Result index() {
        Http.Request request = request();
        String url = routes.HomeController.chat().webSocketURL(request);
        return Results.ok(views.html.index.render(url, webJarsUtil));
    }

    public WebSocket chat() {
        return WebSocket.Text.acceptOrResult(request -> {
            if (sameOriginCheck(request)) {
                return CompletableFuture.completedFuture(F.Either.Right(userFlow));
            } else {
                return CompletableFuture.completedFuture(F.Either.Left(forbidden()));
            }
        });
    }

    /**
     * Checks that the WebSocket comes from the same origin.  This is necessary to protect
     * against Cross-Site WebSocket Hijacking as WebSocket does not implement Same Origin Policy.
     *
     * See https://tools.ietf.org/html/rfc6455#section-1.3 and
     * http://blog.dewhurstsecurity.com/2013/08/30/security-testing-html5-websockets.html
     */
    private boolean sameOriginCheck(Http.RequestHeader request) {
        String[] origins = request.headers().get("Origin");
        if (origins.length > 1) {
            // more than one origin found
            return false;
        }
        String origin = origins[0];
        return originMatches(origin);
    }

    private boolean originMatches(String origin) {
        if (origin == null) return false;
        try {
            URI url = new URI(origin);
            return url.getHost().equals("localhost")
                    && (url.getPort() == 9000 || url.getPort() == 19001);
        } catch (Exception e ) {
            return false;
        }
    }

}
