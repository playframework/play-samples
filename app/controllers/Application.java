package controllers;


import actors.UnwatchStock;
import actors.WatchStock;
import akka.actor.ActorRef;
import akka.dispatch.Mapper;
import org.codehaus.jackson.JsonNode;
import play.Play;
import static akka.pattern.Patterns.ask;

import play.libs.Akka;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;

import actors.Listen;
import utils.Global;

import java.util.List;

public class Application extends Controller {

    public static Result index() {
        return ok(views.html.index.render());
    }

    public static WebSocket<JsonNode> listen(final String uuid) {
        return new WebSocket<JsonNode>() {
            public void onReady(WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out) {
                ask(Global.usersActor, new Listen(uuid, out), 10000).map(
                        new Mapper<Object, Result>() {
                            public Result apply(Object userActor) {
                                // watch the default stocks
                                List<String> defaultStocks = Play.application().configuration().getStringList("default.stocks");
                                for (String symbol : defaultStocks) {
                                    ((ActorRef)userActor).tell(new WatchStock(uuid, symbol), (ActorRef)userActor);
                                }

                                return ok();
                            }
                        }, Akka.system().dispatcher()
                );
            }
        };
    }

    public static Result watch(String uuid, String symbol) {
        Global.usersActor.tell(new WatchStock(uuid, symbol), Global.usersActor);
        return ok();
    }

    public static Result unwatch(String uuid, String symbol) {
        Global.usersActor.tell(new UnwatchStock(uuid, symbol), Global.usersActor);
        return ok();
    }

}