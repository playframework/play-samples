package actors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import play.libs.Json;
import play.mvc.WebSocket;
import play.Play;
import utils.LambdaActor;

/**
 * The broker between the WebSocket and the StockActor(s).  The UserActor holds the connection and sends serialized
 * JSON data to the client.
 */

public class UserActor extends LambdaActor {

    final WebSocket.Out<JsonNode> out;

    public UserActor(WebSocket.Out<JsonNode> out) {
        this.out = out;

        // watch the default stocks
        List<String> defaultStocks = Play.application().configuration().getStringList("default.stocks");

        for (String stockSymbol : defaultStocks) {
            StocksActor.stocksActor().tell(new Stock.Watch(stockSymbol), self());
        }

        receive(Stock.Update.class, stockUpdate -> {
            // push the stock to the client
            JsonNode message =
                Json.newObject()
                    .put("type", "stockupdate")
                    .put("symbol", stockUpdate.symbol)
                    .put("price", stockUpdate.price);
            out.write(message);
        });

        receive(Stock.History.class, stockHistory -> {
            // push the history to the client
            ObjectNode message =
                Json.newObject()
                    .put("type", "stockhistory")
                    .put("symbol", stockHistory.symbol);

            ArrayNode historyJson = message.putArray("history");
            for (Double price : stockHistory.history) {
                historyJson.add(price);
            }

            out.write(message);
        });
    }
}
