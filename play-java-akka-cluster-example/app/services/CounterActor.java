package services;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

/**
 * Counter actor based on https://doc.akka.io/docs/akka/current/typed/cluster-singleton.html#example
 * Modifications: `Increment` includes a replyTo and responds with the current value.
 */
public class CounterActor extends AbstractBehavior<CounterActor.Command> {

    public interface Command {}

    public static class Increment implements Command {
        private final ActorRef<Integer> replyTo;

        public Increment(ActorRef<Integer> replyTo) {
            this.replyTo = replyTo;
        }
    }

    public static class GetValue implements Command {
        private final ActorRef<Integer> replyTo;

        public GetValue(ActorRef<Integer> replyTo) {
            this.replyTo = replyTo;
        }
    }

    public enum GoodByeCounter implements Command {
        INSTANCE
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
                .onMessage(GoodByeCounter.class, msg -> onGoodByCounter())
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

    private Behavior<Command> onGoodByCounter() {
        // Possible async action then stop
        return this;
    }
}
