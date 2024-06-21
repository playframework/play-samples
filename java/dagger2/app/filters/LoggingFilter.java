package filters;

import org.apache.pekko.stream.Materializer;
import org.slf4j.LoggerFactory;
import play.mvc.Filter;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

public class LoggingFilter extends Filter {

    private org.slf4j.Logger logger = LoggerFactory.getLogger("application");

    @Inject
    public LoggingFilter(Materializer mat) {
        super(mat);
    }

    @Override
    public CompletionStage<Result> apply(
            Function<Http.RequestHeader, CompletionStage<Result>> nextFilter,
            Http.RequestHeader requestHeader) {
        long startTime = System.currentTimeMillis();
        return nextFilter.apply(requestHeader).thenApply(result -> {
            long endTime = System.currentTimeMillis();
            long requestTime = endTime - startTime;

            logger.info("{} {} took {}ms and returned {}",
                    requestHeader.method(), requestHeader.uri(), requestTime, result.status());

            return result.withHeader("Request-Time", "" + requestTime);
        });
    }
}
