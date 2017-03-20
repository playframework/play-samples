package v1.post;

import akka.actor.ActorSystem;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import net.jodah.failsafe.AsyncFailsafe;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import net.jodah.failsafe.util.concurrent.Scheduler;
import play.Logger;
import play.libs.concurrent.Futures;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import scala.concurrent.duration.FiniteDuration;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.*;
import java.util.function.Function;

import static com.codahale.metrics.MetricRegistry.name;
import static play.mvc.Http.Status.GATEWAY_TIMEOUT;
import static play.mvc.Http.Status.NOT_ACCEPTABLE;

public class PostAction extends play.mvc.Action.Simple {
    private final Logger.ALogger logger = play.Logger.of("application.PostAction");

    private final Meter requestsMeter;
    private final Timer responsesTimer;
    private final HttpExecutionContext ec;

    @Singleton
    @Inject
    public PostAction(MetricRegistry metrics, HttpExecutionContext ec, ActorSystem actorSystem) {
        this.ec = ec;
        this.requestsMeter = metrics.meter("requestsMeter");
        this.responsesTimer = metrics.timer(name(PostAction.class, "responsesTimer"));
    }

    public CompletionStage<Result> call(Http.Context ctx) {
        if (logger.isDebugEnabled()) {
            logger.debug("call: ctx = " + ctx);
        }
        requestsMeter.mark();
        if (ctx.request().accepts("application/json")) {
            final Timer.Context time = responsesTimer.time();
            return timeout(doCall(ctx), 1L, TimeUnit.SECONDS).whenComplete((r, e) -> time.close());
        } else {
            return CompletableFuture.completedFuture(
                    status(NOT_ACCEPTABLE, "We only accept application/json")
            );
        }
    }

    private CompletionStage<Result> doCall(Http.Context ctx) {
        return delegate.call(ctx).handleAsync((result, e) -> {
            if (e != null) {
                logger.error(e.getMessage(), e);
                return internalServerError();
            } else {
                return result;
            }
        }, ec.current());
    }

    private CompletionStage<Result> timeout(final CompletionStage<Result> stage, final long delay, final TimeUnit unit) {
        final CompletionStage<Result> timeoutFuture = Futures.timeout(delay, unit).handle((v, e) -> {
            return Results.status(GATEWAY_TIMEOUT, views.html.timeout.render());
        });
        return stage.applyToEither(timeoutFuture, Function.identity());
    }

}
