package v1.post;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import net.jodah.failsafe.FailsafeException;
import play.Logger;
import play.libs.concurrent.Futures;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import static com.codahale.metrics.MetricRegistry.name;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static play.mvc.Http.Status.*;

public class PostAction extends play.mvc.Action.Simple {
    private final Logger.ALogger logger = play.Logger.of("application.PostAction");

    private final Meter requestsMeter;
    private final Timer responsesTimer;
    private final HttpExecutionContext ec;
    private final Futures futures;

    @Singleton
    @Inject
    public PostAction(MetricRegistry metrics, HttpExecutionContext ec, Futures futures) {
        this.ec = ec;
        this.futures = futures;
        this.requestsMeter = metrics.meter("requestsMeter");
        this.responsesTimer = metrics.timer(name(PostAction.class, "responsesTimer"));
    }

    public CompletionStage<Result> call(Http.Context ctx) {
        if (logger.isTraceEnabled()) {
            logger.trace("call: ctx = " + ctx);
        }

        requestsMeter.mark();
        if (ctx.request().accepts("application/json")) {
            final Timer.Context time = responsesTimer.time();
            return futures.timeout(doCall(ctx), 1L, TimeUnit.SECONDS).exceptionally(e -> {
                return (Results.status(GATEWAY_TIMEOUT, views.html.timeout.render()));
            }).whenComplete((r, e) -> time.close());
        } else {
            return completedFuture(
                    status(NOT_ACCEPTABLE, "We only accept application/json")
            );
        }
    }

    private CompletionStage<Result> doCall(Http.Context ctx) {
        return delegate.call(ctx).handleAsync((result, e) -> {
            if (e != null) {
                if (e instanceof CompletionException) {
                    Throwable completionException = e.getCause();
                    if (completionException instanceof FailsafeException) {
                        logger.error("Circuit breaker is open!", completionException);
                        return Results.status(SERVICE_UNAVAILABLE, "Service has timed out");
                    } else {
                        logger.error("Direct exception " + e.getMessage(), e);
                        return internalServerError();
                    }
                } else {
                    logger.error("Unknown exception " + e.getMessage(), e);
                    return internalServerError();
                }
            } else {
                return result;
            }
        }, ec.current());
    }
}
