package services;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Counter actor based on https://doc.akka.io/docs/akka/2.6/typed/cluster-singleton.html#example
 * Modifications:
 *  - added `replyTo` field to `Increment` message so we can respond with the current counter value.
 *  - removed unused message from the protocol (and corresponding handler).
 *
 * NOTE: This example application uses a transient counter. Every time the Cluster Singleton were this counter
 * reside moves from a node to another (when the cluster members change singletons may be relocated) the in-memory
 * counter will be wiped. To make this counter durable use
 * [Akka Persistence](https://doc.akka.io/docs/akka/2.6/typed/persistence.html#example-and-core-api).
 *
 * TODO: `CounterActor` should extend `EventSourcedBehavior` from Akka Persistence to make the count durable.
 */
public class CounterActor extends AbstractBehavior<CounterActor.Command> {

    public interface Command extends CborSerializable{}

    public static class Increment implements Command {
        private final ActorRef<Integer> replyTo;

        @JsonCreator
        public Increment(ActorRef<Integer> replyTo) {
            this.replyTo = replyTo;
        }
    }

    public static class GetValue implements Command {
        private final ActorRef<Integer> replyTo;

        @JsonCreator
        public GetValue(ActorRef<Integer> replyTo) {
            this.replyTo = replyTo;
        }
    }


    public static Behavior<Command> create() {
        return Behaviors.setup(CounterActor::new);
    }

    private int value = 0;

    private CounterActor(ActorContext<Command> context) {
        super(context);
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(Increment.class, this::onIncrement)
                .onMessage(GetValue.class, this::onGetValue)
                .build();
    }

    private Behavior<Command> onIncrement(Increment msg) {
        value++;
        msg.replyTo.tell(value);
        return this;
    }

    private Behavior<Command> onGetValue(GetValue msg) {
        msg.replyTo.tell(value);
        return this;
    }

}
