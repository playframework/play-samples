package controllers;

import actors.*;
import akka.actor.*;
import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Optional;
import play.libs.Akka;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import play.Play;

/**
 * The main web controller that handles returning the index page, setting up a WebSocket, and watching a stock.
 */
public class Application extends Controller {

    public Result index() {
        return ok(views.html.index.render());
    }

    public WebSocket<JsonNode> ws() {
        return WebSocket.whenReady((in, out) -> {
            // create a new UserActor and give it the default stocks to watch
            final ActorRef userActor = Akka.system().actorOf(Props.create(UserActor.class, out));
            List<String> defaultStocks = Play.application().configuration().getStringList("default.stocks");
            for (String stockSymbol : defaultStocks) {
                StocksActor.stocksActor().tell(new Stock.Watch(stockSymbol), userActor);
            }

            // send all WebSocket message to the UserActor
            in.onMessage(jsonNode -> {
                // parse the JSON into Stock.Watch
                Stock.Watch watchStock = new Stock.Watch(jsonNode.get("symbol").textValue());
                // send the watchStock message to the StocksActor
                StocksActor.stocksActor().tell(watchStock, userActor);
            });

            // on close, tell the userActor to shutdown
            in.onClose(() -> {
                StocksActor.stocksActor().tell(new Stock.Unwatch(Optional.empty()), userActor);
                Akka.system().stop(userActor);
            });
        });
    }

}
