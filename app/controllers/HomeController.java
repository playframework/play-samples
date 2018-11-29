package controllers;

import actors.UserParentActor;
import akka.NotUsed;
import akka.actor.ActorRef;
import akka.stream.javadsl.Flow;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.webjars.play.WebJarsUtil;
import play.libs.F.Either;
import play.mvc.*;


import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static akka.pattern.PatternsCS.ask;

/**
 * The main web controller that handles returning the index page, setting up a WebSocket, and watching a stock.
 */
@Singleton
public class HomeController extends Controller {

    private final Duration t = Duration.of(1, ChronoUnit.SECONDS);
    private final Logger logger = org.slf4j.LoggerFactory.getLogger("controllers.HomeController");
    private final ActorRef userParentActor;

    private WebJarsUtil webJarsUtil;

    @Inject
    public HomeController(@Named("userParentActor") ActorRef userParentActor, WebJarsUtil webJarsUtil) {
        this.userParentActor = userParentActor;
        this.webJarsUtil = webJarsUtil;
    }

    public Result index(Http.Request request) {
        return ok(views.html.index.render(request, webJarsUtil));
    }

    public WebSocket ws() {
        return WebSocket.Json.acceptOrResult(request -> {
            if (sameOriginCheck(request)) {
                final CompletionStage<Flow<JsonNode, JsonNode, NotUsed>> future = wsFutureFlow(request);
                final CompletionStage<Either<Result, Flow<JsonNode, JsonNode, ?>>> stage = future.thenApply(Either::Right);
                return stage.exceptionally(this::logException);
            } else {
                return forbiddenResult();
            }
        });
    }

    @SuppressWarnings("unchecked")
    private CompletionStage<Flow<JsonNode, JsonNode, NotUsed>> wsFutureFlow(Http.RequestHeader request) {
        long id = request.asScala().id();
        UserParentActor.Create create = new UserParentActor.Create(Long.toString(id));

        return ask(userParentActor, create, t).thenApply((Object flow) -> {
            final Flow<JsonNode, JsonNode, NotUsed> f = (Flow<JsonNode, JsonNode, NotUsed>) flow;
            return f.named("websocket");
        });
    }

    private CompletionStage<Either<Result, Flow<JsonNode, JsonNode, ?>>> forbiddenResult() {
        final Result forbidden = Results.forbidden("forbidden");
        final Either<Result, Flow<JsonNode, JsonNode, ?>> left = Either.Left(forbidden);

        return CompletableFuture.completedFuture(left);
    }

    private Either<Result, Flow<JsonNode, JsonNode, ?>> logException(Throwable throwable) {
        logger.error("Cannot create websocket", throwable);
        Result result = Results.internalServerError("error");
        return Either.Left(result);
    }

    /**
     * Checks that the WebSocket comes from the same origin.  This is necessary to protect
     * against Cross-Site WebSocket Hijacking as WebSocket does not implement Same Origin Policy.
     * <p>
     * See https://tools.ietf.org/html/rfc6455#section-1.3 and
     * http://blog.dewhurstsecurity.com/2013/08/30/security-testing-html5-websockets.html
     */
    private boolean sameOriginCheck(Http.RequestHeader rh) {
        final Optional<String> origin = rh.header("Origin");

        if (! origin.isPresent()) {
            logger.error("originCheck: rejecting request because no Origin header found");
            return false;
        } else if (originMatches(origin.get())) {
            logger.debug("originCheck: originValue = " + origin);
            return true;
        } else {
            logger.error("originCheck: rejecting request because Origin header value " + origin + " is not in the same origin: "
                + String.join(", ", validOrigins));
            return false;
        }
    }

    private List<String> validOrigins = Arrays.asList("localhost:9000", "localhost:19001");
    private boolean originMatches(String actualOrigin) {
        return validOrigins.stream().anyMatch(actualOrigin::contains);
    }

}
