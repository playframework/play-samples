package utils;

import actors.StocksActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.Akka;

public class Global extends GlobalSettings {

    @Override
    public void onStart(Application app) {
        ActorRef stocksActor = Akka.system().actorOf(Props.apply(StocksActor.class), "stocks");
        Logger.info(stocksActor.toString());
        super.onStart(app);
    }

}
