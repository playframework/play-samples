package actors;

import akka.actor.UntypedActor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.Play;
import play.libs.Json;
import play.mvc.WebSocket;

import java.util.List;

/**
 * The broker between the WebSocket and the StockActor(s).  The UserActor holds the connection and sends serialized
 * JSON data to the client.
 */

public class UserActor extends UntypedActor {

    private final WebSocket.Out<JsonNode> out;

    public UserActor(WebSocket.Out<JsonNode> out) {
        this.out = out;

        // watch the default stocks
        List<String> defaultStocks = Play.application().configuration().getStringList("default.stocks");

        for (String stockSymbol : defaultStocks) {
            StocksActor.stocksActor().tell(new Stock.Watch(stockSymbol), getSelf());
        }
    }

    public void onReceive(Object message) {
        if (message instanceof Stock.Update) {
            // push the stock to the client
            Stock.Update stockUpdate = (Stock.Update) message;
            ObjectNode stockUpdateMessage = Json.newObject();
            stockUpdateMessage.put("type", "stockupdate");
            stockUpdateMessage.put("symbol", stockUpdate.symbol);
            stockUpdateMessage.put("price", stockUpdate.price);
            out.write(stockUpdateMessage);
        }
        else if (message instanceof Stock.History) {
            // push the history to the client
            Stock.History stockHistory = (Stock.History) message;

            ObjectNode stockUpdateMessage = Json.newObject();
            stockUpdateMessage.put("type", "stockhistory");
            stockUpdateMessage.put("symbol", stockHistory.symbol);

            ArrayNode historyJson = stockUpdateMessage.putArray("history");
            for (Double price : stockHistory.history) {
                historyJson.add(price);
            }

            out.write(stockUpdateMessage);
        }
    }
}
