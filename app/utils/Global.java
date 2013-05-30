package utils;

import actors.FetchLatest;
import actors.StockHolderActor;
import actors.UsersActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import play.Application;
import play.GlobalSettings;
import play.Play;
import play.libs.Akka;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

public class Global extends GlobalSettings {

    public static String sentimentUrl;
    public static String farooUrl;
    
    public static ActorRef usersActor;
    public static ActorRef stockHolderActor;
    
    @Override
    public void onStart(Application application) {

        farooUrl = Play.application().configuration().getString("faroo.url");
        sentimentUrl = Play.application().configuration().getString("sentiment.url");
        
        if ((sentimentUrl == null) || (farooUrl == null)) {
            throw new RuntimeException("Both sentiment.url and faroo.url configs must be specified");
        }
        
        usersActor = Akka.system().actorOf(new Props(UsersActor.class), "users");
        
        stockHolderActor = Akka.system().actorOf(new Props(StockHolderActor.class), "stocks");

        // fetch a new data point once every second
        Akka.system().scheduler().schedule(Duration.Zero(), Duration.create(50, TimeUnit.MILLISECONDS), stockHolderActor, FetchLatest.instance(), Akka.system().dispatcher());

        super.onStart(application);
    }
}
