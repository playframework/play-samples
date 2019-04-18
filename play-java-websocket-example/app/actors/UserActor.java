package actors;

import actors.Messages.Stocks;
import actors.Messages.UnwatchStocks;
import actors.Messages.WatchStocks;
import akka.Done;
import akka.NotUsed;
import akka.actor.AbstractActor;
import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Pair;
import akka.stream.KillSwitches;
import akka.stream.Materializer;
import akka.stream.UniqueKillSwitch;
import akka.stream.javadsl.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.assistedinject.Assisted;
import play.libs.Json;
import stocks.Stock;

import javax.inject.Inject;
import javax.inject.Named;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionStage;

import static akka.pattern.PatternsCS.ask;

/**
 * The broker between the WebSocket and the StockActor(s).  The UserActor holds the connection and sends serialized
 * JSON data to the client.
 */
public class UserActor extends AbstractActor {

    private final Duration timeout = Duration.of(5, ChronoUnit.MILLIS);

    private final LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

    private final Map<String, UniqueKillSwitch> stocksMap = new HashMap<>();

    private final String id;
    private final ActorRef stocksActor;
    private final Materializer mat;

    private final Sink<JsonNode, NotUsed> hubSink;
    private final Flow<JsonNode, JsonNode, NotUsed> websocketFlow;

    @Inject
    public UserActor(@Assisted String id,
                     @Named("stocksActor") ActorRef stocksActor,
                     Materializer mat) {
        this.id = id;
        this.stocksActor = stocksActor;
        this.mat = mat;

        Pair<Sink<JsonNode, NotUsed>, Source<JsonNode, NotUsed>> sinkSourcePair =
                MergeHub.of(JsonNode.class, 16)
                .toMat(BroadcastHub.of(JsonNode.class, 256), Keep.both())
                .run(mat);

        this.hubSink = sinkSourcePair.first();
        Source<JsonNode, NotUsed> hubSource = sinkSourcePair.second();

        Sink<JsonNode, CompletionStage<Done>> jsonSink = Sink.foreach((JsonNode json) -> {
            // When the user types in a stock in the upper right corner, this is triggered,
            String symbol = json.findPath("symbol").asText();
            addStocks(Collections.singleton(symbol));
        });

        // Put the source and sink together to make a flow of hub source as output (aggregating all
        // stocks as JSON to the browser) and the actor as the sink (receiving any JSON messages
        // from the browse), using a coupled sink and source.
        this.websocketFlow = Flow.fromSinkAndSourceCoupled(jsonSink, hubSource)
                //.log("actorWebsocketFlow", logger)
                .watchTermination((n, stage) -> {
                    // When the flow shuts down, make sure this actor also stops.
                    stage.thenAccept(f -> context().stop(self()));
                    return NotUsed.getInstance();
                });
    }

    /**
     * The receive block, useful if other actors want to manipulate the flow.
     */
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(WatchStocks.class, watchStocks -> {
                    logger.info("Received message {}", watchStocks);
                    addStocks(watchStocks.symbols);
                    sender().tell(websocketFlow, self());
                })
                .match(UnwatchStocks.class, unwatchStocks -> {
                    logger.info("Received message {}", unwatchStocks);
                    unwatchStocks(unwatchStocks.symbols);
                }).build();
    }

    /**
     * Adds several stocks to the hub, by asking the stocks actor for stocks.
     */
    private void addStocks(Set<String> symbols) {
        // Ask the stocksActor for a stream containing these stocks.
        CompletionStage<Stocks> future = ask(stocksActor, new WatchStocks(symbols), timeout).thenApply(Stocks.class::cast);

        // when we get the response back, we want to turn that into a flow by creating a single
        // source and a single sink, so we merge all of the stock sources together into one by
        // pointing them to the hubSink, so we can add them dynamically even after the flow
        // has started.
        future.thenAccept((Stocks newStocks) -> {
            newStocks.stocks.forEach(stock -> {
                if (!stocksMap.containsKey(stock.symbol)) {
                    addStock(stock);
                }
            });
        });
    }

    /**
     * Adds a single stock to the hub.
     */
    private void addStock(Stock stock) {
        logger.info("Adding stock {}", stock);

        // We convert everything to JsValue so we get a single stream for the websocket.
        // Make sure the history gets written out before the updates for this stock...
        final Source<JsonNode, NotUsed> historySource = stock.history(50).map(Json::toJson);
        final Source<JsonNode, NotUsed> updateSource = stock.update().map(Json::toJson);
        final Source<JsonNode, NotUsed> stockSource = historySource.concat(updateSource);

        // Set up a flow that will let us pull out a killswitch for this specific stock,
        // and automatic cleanup for very slow subscribers (where the browser has crashed, etc).
        final Flow<JsonNode, JsonNode, UniqueKillSwitch> killswitchFlow = Flow.of(JsonNode.class)
                .joinMat(KillSwitches.singleBidi(), Keep.right());
        // Set up a complete runnable graph from the stock source to the hub's sink
        String name = "stock-" + stock.symbol + "-" + id;
        final RunnableGraph<UniqueKillSwitch> graph = stockSource
                .viaMat(killswitchFlow, Keep.right())
                .to(hubSink)
                .named(name);

        // Start it up!
        UniqueKillSwitch killSwitch = graph.run(mat);

        // Pull out the kill switch so we can stop it when we want to unwatch a stock.
        stocksMap.put(stock.symbol, killSwitch);
    }

    private void unwatchStocks(Set<String> symbols) {
        symbols.forEach(symbol -> {
            stocksMap.get(symbol).shutdown();
            stocksMap.remove(symbol);
        });
    }

    public interface Factory {
        Actor create(String id);
    }
}
