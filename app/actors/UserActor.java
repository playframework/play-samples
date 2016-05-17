package actors;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.assistedinject.Assisted;
import play.Configuration;
import play.libs.Json;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

/**
 * The broker between the WebSocket and the StockActor(s).  The UserActor holds the connection and sends serialized
 * JSON data to the client.
 */
public class UserActor extends UntypedActor {

    private LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

    private ActorRef out;
    private Configuration configuration;
    private ActorRef stocksActor;

    @Inject
    public UserActor(@Assisted ActorRef out,
                     @Named("stocksActor") ActorRef stocksActor,
                     Configuration configuration) {
        this.out = out;
        this.stocksActor = stocksActor;
        this.configuration = configuration;
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();

        configureDefaultStocks();
    }

    public void configureDefaultStocks() {
        List<String> defaultStocks = configuration.getStringList("default.stocks");
        logger.info("Creating user actor with default stocks {}", defaultStocks);

        for (String stockSymbol : defaultStocks) {
            stocksActor.tell(new Stock.Watch(stockSymbol), self());
        }
    }

    public void onReceive(Object msg) throws Exception {

        if (msg instanceof Stock.Update) {
            Stock.Update stockUpdate = (Stock.Update) msg;
            // push the stock to the client
            JsonNode message =
                    Json.newObject()
                            .put("type", "stockupdate")
                            .put("symbol", stockUpdate.symbol)
                            .put("price", stockUpdate.price);

            logger.debug("onReceive: " + message);

            out.tell(message, self());
        }

        if (msg instanceof Stock.History) {
            Stock.History stockHistory = (Stock.History) msg;
            // push the history to the client
            ObjectNode message =
                    Json.newObject()
                            .put("type", "stockhistory")
                            .put("symbol", stockHistory.symbol);

            ArrayNode historyJson = message.putArray("history");
            for (Double price : stockHistory.history) {
                historyJson.add(price);
            }

            logger.debug("onReceive: " + message);

            out.tell(message, self());
        }

        if (msg instanceof JsonNode) {
            // When the user types in a stock in the upper right corner, this is triggered
            JsonNode json = (JsonNode) msg;
            final String symbol = json.get("symbol").textValue();
            stocksActor.tell(new Stock.Watch(symbol), self());
        }
    }

    public interface Factory {
        Actor create(ActorRef out);
    }
}
