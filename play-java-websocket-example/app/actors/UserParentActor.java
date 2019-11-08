package actors;

import akka.NotUsed;
import akka.actor.AbstractActor;
import akka.actor.typed.ActorRef;
import akka.actor.typed.javadsl.Adapter;
import akka.stream.javadsl.Flow;
import com.fasterxml.jackson.databind.JsonNode;
import com.typesafe.config.Config;
import play.libs.akka.InjectedActorSupport;

import javax.inject.Inject;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

public class UserParentActor extends AbstractActor implements InjectedActorSupport {

    private final Duration timeout = Duration.ofSeconds(2);
    private final Set<String> defaultStocks;

    public static class Create {
        final String id;
        final ActorRef<Flow<JsonNode, JsonNode, NotUsed>> replyTo;

        public Create(String id, ActorRef<Flow<JsonNode, JsonNode, NotUsed>> replyTo) {
            this.id = id;
            this.replyTo = replyTo;
        }
    }

    private final UserActor.Factory childFactory;

    @Inject
    public UserParentActor(UserActor.Factory childFactory, Config config) {
        this.childFactory = childFactory;
        this.defaultStocks = new HashSet<>(config.getStringList("default.stocks"));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(UserParentActor.Create.class, create -> {
                    ActorRef<UserActor.Message> child =
                        Adapter.spawn(getContext(), childFactory.create(create.id), "userActor-" + create.id);
                    child.tell(new UserActor.WatchStocks(defaultStocks, create.replyTo));
                }).build();
    }

}
