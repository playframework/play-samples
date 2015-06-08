package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.stream.Stream;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import play.mvc.*;
import play.Play;
import static java.util.stream.Collectors.averagingDouble;
import static java.util.stream.Collectors.toList;
import static play.libs.F.Promise;
import static utils.Streams.stream;

public class StockSentiment extends Controller {

    public Promise<Result> get(String symbol) {
        return fetchTweets(symbol)
               .flatMap(StockSentiment::fetchSentiments)
               .map(StockSentiment::averageSentiment)
               .<Result>map(Results::ok)
               .recover(StockSentiment::errorResponse);
    }

    private static Promise<List<String>> fetchTweets(String symbol) {
        return WS.url(Play.application().configuration().getString("tweet.url"))
                 .setQueryParameter("q", "$" + symbol).get()
                 .filter(response -> response.getStatus() == Http.Status.OK)
                 .map(response -> stream(response.asJson().findPath("statuses"))
                                  .map(s -> s.findValue("text").asText())
                                  .collect(toList()));
    }

    private static Promise<List<JsonNode>> fetchSentiments(List<String> tweets) {
        String url = Play.application().configuration().getString("sentiment.url");
        Stream<Promise<WSResponse>> sentiments = tweets.stream().map(text -> WS.url(url).post("text=" + text));
        return Promise.sequence(sentiments::iterator).map(StockSentiment::responsesAsJson);
    }

    private static List<JsonNode> responsesAsJson(List<WSResponse> responses) {
        return responses.stream().map(WSResponse::asJson).collect(toList());
    }

    private static JsonNode averageSentiment(List<JsonNode> sentiments) {
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

    private static double collectAverage(List<JsonNode> jsons, String label) {
        return jsons.stream().collect(averagingDouble(json -> json.findValue(label).asDouble()));
    }

    private static Result errorResponse(Throwable ignored) {
        return internalServerError(Json.newObject().put("error", "Could not fetch the tweets"));
    }
}
