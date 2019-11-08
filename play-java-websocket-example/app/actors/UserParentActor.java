package actors;

import akka.NotUsed;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;
import akka.stream.javadsl.Flow;
import com.fasterxml.jackson.databind.JsonNode;
import com.typesafe.config.Config;

import java.util.HashSet;
import java.util.Set;

public final class UserParentActor {
    private UserParentActor() {}

    public static final class Create {
        final String id;
        final ActorRef<Flow<JsonNode, JsonNode, NotUsed>> replyTo;

        public Create(String id, ActorRef<Flow<JsonNode, JsonNode, NotUsed>> replyTo) {
            this.id = id;
            this.replyTo = replyTo;
        }
    }

    public static Behavior<Create> create(UserActor.Factory childFactory, Config config) {
        return Behaviors.setup(context -> {
            Set<String>defaultStocks = new HashSet<>(config.getStringList("default.stocks"));
            Behavior<Create> behavior = Behaviors.receive(Create.class)
                .onMessage(Create.class, create -> {
                    ActorRef<UserActor.Message> child = context.spawn(childFactory.create(create.id), "userActor-" + create.id);
                    child.tell(new UserActor.WatchStocks(defaultStocks, create.replyTo));
                    return Behaviors.same();
                })
                .build();
            return Behaviors.logMessages(behavior);
        });
    }
}
