package controllers;

import actors.*;
import akka.actor.*;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import org.codehaus.jackson.JsonNode;
import play.Play;
import play.libs.Akka;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;

import java.util.List;

/**
 * The main web controller that handles returning the index page, setting up a WebSocket, and watching a stock.
 */
public class Application extends Controller {

    public static Result index() {
        return ok(views.html.index.render());
    }

    public static WebSocket<JsonNode> listen(final String uuid) {
        return new WebSocket<JsonNode>() {
            public void onReady(final WebSocket.In<JsonNode> in, final WebSocket.Out<JsonNode> out) {

                List<String> defaultStocks = Play.application().configuration().getStringList("default.stocks");

                // create a new UserActor and give it the default stocks to watch
                final ActorRef userActor = Akka.system().actorOf(Props.create(UserActor.class, out, uuid, defaultStocks), uuid);
                
                for (String symbol : defaultStocks) {
                    watch(uuid, symbol);
                }

                in.onClose(new F.Callback0() {
                    @Override
                    public void invoke() throws Throwable {
                        userActor.tell(new CleanupWatchers(), Akka.system().deadLetters());
                        Akka.system().stop(userActor);
                    }
                });
                
            }
        };
    }

    public static Result watch(String uuid, String symbol) {
        StocksActor.stocksActor().tell(new SetupStock(uuid, symbol), Akka.system().deadLetters());
        return ok();
    }

}