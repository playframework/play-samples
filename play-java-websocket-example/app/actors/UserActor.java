package actors;

import actors.StocksActor.Stocks;
import actors.StocksActor.GetStocks;
import akka.Done;
import akka.NotUsed;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.Scheduler;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.japi.Pair;
import akka.stream.KillSwitches;
import akka.stream.Materializer;
import akka.stream.UniqueKillSwitch;
import akka.stream.javadsl.*;
import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;
import stocks.Stock;

import javax.inject.Inject;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionStage;

import static akka.actor.typed.javadsl.AskPattern.ask;
import static java.util.Objects.requireNonNull;

/**
 * The broker between the WebSocket and the StockActor(s).  The UserActor holds the connection and sends serialized
 * JSON data to the client.
 */
public class UserActor {
    public interface Message {}

    public static final class WatchStocks implements Message {
        final Set<String> symbols;
        final ActorRef<Flow<JsonNode, JsonNode, NotUsed>> replyTo;

        public WatchStocks(Set<String> symbols, ActorRef<Flow<JsonNode, JsonNode, NotUsed>> replyTo) {
            this.symbols = requireNonNull(symbols);
            this.replyTo = requireNonNull(replyTo);
        }

        @Override
        public String toString() {
            return "WatchStocks(" + symbols + ", " + replyTo + ")";
        }
    }

    public static final class UnwatchStocks implements Message {
        final Set<String> symbols;

        public UnwatchStocks(Set<String> symbols) {
            this.symbols = requireNonNull(symbols);
        }

        @Override
        public String toString() {
            return "UnwatchStocks(" + symbols + ")";
        }
    }

    private static final class InternalStop implements Message {
        private static final InternalStop INSTANCE = new InternalStop();
        public static InternalStop get() {
            return INSTANCE;
        }
        private InternalStop() {}
    }

    private final Duration timeout = Duration.of(5, ChronoUnit.MILLIS);

    private final Map<String, UniqueKillSwitch> stocksMap = new HashMap<>();

    private final String id;
    private final ActorRef<StocksActor.GetStocks> stocksActor;
    private final Materializer mat;
    private final Scheduler scheduler;
    private final ActorContext<Message> context;

    private final Sink<JsonNode, NotUsed> hubSink;
    private final Flow<JsonNode, JsonNode, NotUsed> websocketFlow;

    public static Behavior<Message> create(String id, ActorRef<GetStocks> stocksActor) {
        return Behaviors.setup(context -> new UserActor(id, stocksActor, context).behavior());
    }

    @Inject
    public UserActor(String id,
                     ActorRef<GetStocks> stocksActor,
                     ActorContext<Message> context) {
        this.id = id;
        this.stocksActor = stocksActor;
        this.mat = Materializer.matFromSystem(context.getSystem());
        this.scheduler = context.getSystem().scheduler();
        this.context = context;

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
                    context.pipeToSelf(stage, (Done _done, Throwable _throwable) -> InternalStop.get());
                    return NotUsed.getInstance();
                });
    }

    public Behavior<Message> behavior() {
        return Behaviors
            .receive(Message.class)
            .onMessage(WatchStocks.class, watchStocks -> {
                context.getLog().info("Received message {}", watchStocks);
                addStocks(watchStocks.symbols);
                watchStocks.replyTo.tell(websocketFlow);
                return Behaviors.same();
            })
            .onMessage(UnwatchStocks.class, unwatchStocks -> {
                context.getLog().info("Received message {}", unwatchStocks);
                unwatchStocks(unwatchStocks.symbols);
                return Behaviors.same();
            })
            .onMessageEquals(InternalStop.get(), Behaviors::stopped)
            .onSignal(PostStop.class, _postStop -> {
                // If this actor is killed directly, stop anything that we started running explicitly.
                context.getLog().info("Stopping actor {}", context.getSelf());
                unwatchStocks(stocksMap.keySet());
                return Behaviors.same();
            })
            .build();
    }

    /**
     * Adds several stocks to the hub, by asking the stocks actor for stocks.
     */
    private void addStocks(Set<String> symbols) {
        // Ask the stocksActor for a stream containing these stocks.
        CompletionStage<Stocks> future = ask(stocksActor, replyTo -> new GetStocks(symbols, replyTo), timeout, scheduler);

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
        context.getLog().info("Adding stock {}", stock);

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
        Behavior<Message> create(String id);
    }
}
