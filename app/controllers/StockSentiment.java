package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import play.Configuration;
import play.libs.Json;
import play.libs.concurrent.Futures;
import play.libs.concurrent.HttpExecutionContext;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.averagingDouble;
import static java.util.stream.Collectors.toList;
import static utils.Streams.stream;

@Singleton
public class StockSentiment extends Controller {

    private WSClient wsClient;
    private Configuration configuration;
    private HttpExecutionContext ec;

    @Inject
    public StockSentiment(WSClient wsClient, Configuration configuration, HttpExecutionContext ec) {
        this.wsClient = wsClient;
        this.configuration = configuration;
        this.ec = ec;
    }

    public CompletionStage<Result> get(String symbol) {
        return fetchTweets(symbol)
                .thenComposeAsync(this::fetchSentiments)
                .thenApplyAsync(this::averageSentiment)
                .<Result>thenApplyAsync(Results::ok)
                .exceptionally(this::errorResponse);
    }

    private CompletionStage<List<String>> fetchTweets(String symbol) {

        final String tweetUrl = configuration.getString("tweet.url");
        final CompletionStage<WSResponse> futureResponse = wsClient.url(tweetUrl)
                .setQueryParameter("q", "$" + symbol)
                .get();

        final CompletionStage<WSResponse> filter = futureResponse.thenApplyAsync(response -> {
            if (response.getStatus() == Http.Status.OK) {
                return response;
            } else {
                return null;
            }
        }, ec.current());

        return filter.thenApplyAsync(response -> {
            final List<String> statuses = stream(response.asJson().findPath("statuses"))
                    .map(s -> s.findValue("text").asText())
                    .collect(Collectors.toList());
            return statuses;
        });
    }

    private CompletionStage<List<JsonNode>> fetchSentiments(List<String> tweets) {
        String url = configuration.getString("sentiment.url");
        Stream<CompletionStage<WSResponse>> sentiments = tweets.stream().map(text -> {
            return wsClient.url(url).post("text=" + text);
        });
        return Futures.sequence(sentiments::iterator).thenApplyAsync(this::responsesAsJson);
    }

    private List<JsonNode> responsesAsJson(List<WSResponse> responses) {
        return responses.stream().map(WSResponse::asJson).collect(toList());
    }

    private JsonNode averageSentiment(List<JsonNode> sentiments) {
        double neg = collectAverage(sentiments, "neg");
        double neutral = collectAverage(sentiments, "neutral");
        double pos = collectAverage(sentiments, "pos");

        String label = (neutral > 0.5) ? "neutral" : (neg > pos) ? "neg" : "pos";

        return Json.newObject()
                .put("label", label)
                .set("probability", Json.newObject()
                        .put("neg", neg)
                        .put("neutral", neutral)
                        .put("pos", pos));
    }

    private double collectAverage(List<JsonNode> jsons, String label) {
        return jsons.stream().collect(averagingDouble(json -> json.findValue(label).asDouble()));
    }

    private Result errorResponse(Throwable ignored) {
        return internalServerError(Json.newObject().put("error", "Could not fetch the tweets"));
    }
}
