package actors;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;

/**
 * Writing actors in Java is easy.  This class creates child user actors in response to
 * Listen messages, and sends stock messages meant for a specific child actor directly to that child.
 */
public class UsersActor extends UntypedActor {

    public Creator<? extends Actor> getUserActorCreator(final Listen listen) {
        return new Creator<UserActor>() {
            public UserActor create() {
                return new UserActor(listen.uuid(), listen.out());
            }
        };
    }

    public void onReceive(Object message) {

        if (message instanceof StockUpdate) {
            for (ActorRef child : getContext().getChildren()) {
                child.tell(message, getSelf());
            }
        }
        else if (message instanceof Listen) {
            final Listen listen = (Listen)message;
            getSender().tell(getContext().actorOf(Props.apply(getUserActorCreator(listen)), listen.uuid()), getSelf());
        }
        else if (message instanceof WatchStock) {
            final WatchStock watchStock = (WatchStock)message;
            getContext().getChild(watchStock.uuid()).tell(watchStock, getSelf());
        }
        else if (message instanceof UnwatchStock) {
            final UnwatchStock unwatchStock = (UnwatchStock)message;
            getContext().getChild(unwatchStock.uuid()).tell(unwatchStock, getSelf());
        }
    }
}
