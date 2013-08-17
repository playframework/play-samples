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
                final ActorRef userActor = Akka.system().actorOf(Props.create(UserActor.class, out, uuid), uuid);

                for (String symbol : defaultStocks) {
                    watch(uuid, symbol);
                }

                in.onClose(new F.Callback0() {
                    @Override
                    public void invoke() throws Throwable {
                        userActor.tell(new ShutdownUserActor(), ActorRef.noSender());
                    }
                });
                
            }
        };
    }

    public static Result watch(String uuid, String symbol) {

        //actorFor is deprecated, but actorSelection returns an ActorSelection and we want the actual ActorRef.
        //I am not sure how to get the actual ActorRef in this case?
        ActorRef userActorRef = Akka.system().actorFor("/user/" + uuid);

        StocksActor.stocksActor().tell(new SetupStock(uuid, userActorRef, symbol), ActorRef.noSender());
        return ok();
    }

}