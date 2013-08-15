package actors;

import akka.actor.UntypedActor;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import play.libs.Akka;
import play.libs.Json;
import play.mvc.WebSocket;

import java.util.List;

/**
 * The broker between the WebSocket and the StockActor(s).  The UserActor holds the connection and sends serialized
 * JSON data to the client.
 */

public class UserActor extends UntypedActor {

    private final WebSocket.Out<JsonNode> out;
    private final String uuid;
    private List<String> stockWatchList;
    
    public UserActor(WebSocket.Out<JsonNode> out, String uuid, List<String> stockWatchList) {
        this.out = out;
        this.uuid = uuid;
        this.stockWatchList = stockWatchList;
    }
    
    public void onReceive(Object message) {

        if (message instanceof StockUpdate) {

            // push the stock to the client
            StockUpdate stockUpdate = (StockUpdate)message;
            ObjectNode stockUpdateMessage = Json.newObject();
            stockUpdateMessage.put("type", "stockupdate");
            stockUpdateMessage.put("symbol", stockUpdate.symbol());
            stockUpdateMessage.put("price", stockUpdate.price().doubleValue());
            out.write(stockUpdateMessage);
        }
        else if (message instanceof StockHistory) {
            // push the history to the client
            StockHistory stockHistory = (StockHistory)message;

            ObjectNode stockUpdateMessage = Json.newObject();
            stockUpdateMessage.put("type", "stockhistory");
            stockUpdateMessage.put("symbol", stockHistory.symbol());

            ArrayNode historyJson = stockUpdateMessage.putArray("history");
            for (Object price : stockHistory.history()) {
                historyJson.add(((Number)price).doubleValue());
            }
            
            out.write(stockUpdateMessage);
        }
        else if (message  instanceof  CleanupWatchers) {
            for (String symbol : stockWatchList) {
                StocksActor.stocksActor().tell(new RemoveWatcher(uuid, symbol), self());
            }
        }
        else if (message instanceof WatchStock) {
            UserWatchStock userWatchStock = (UserWatchStock) message;
            stockWatchList.add(userWatchStock.symbol());

        }

    }
}