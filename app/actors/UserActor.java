package actors;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;
import play.mvc.WebSocket;

/**
 * The broker between the WebSocket and the StockActor(s).  The UserActor holds the connection and sends serialized
 * JSON data to the client.
 */
public class UserActor extends AbstractActor {

    public UserActor(WebSocket.Out<JsonNode> out) {
        receive(ReceiveBuilder
            .match(Stock.Update.class, stockUpdate -> {
                // push the stock to the client
                JsonNode message =
                    Json.newObject()
                        .put("type", "stockupdate")
                        .put("symbol", stockUpdate.symbol)
                        .put("price", stockUpdate.price);
                out.write(message);
            })
            .match(Stock.History.class, stockHistory -> {
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
            }).build());
    }
}
