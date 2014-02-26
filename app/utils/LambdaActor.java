package utils;

import akka.actor.AbstractActor;
import akka.actor.UntypedActorContext;
import akka.japi.pf.FI;
import akka.japi.pf.ReceiveBuilder;
import akka.japi.pf.UnitPFBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

/**
 * An actor that allows receive builder matches to be specified in the constructor,
 * avoiding boilerplate and Scala types. Also provides the Java actor context.
 */
public class LambdaActor extends AbstractActor {

    private UnitPFBuilder<Object> receiveBuilder = new UnitPFBuilder<Object>();

    public <A> void receive(final Class<A> type, FI.UnitApply<A> apply) {
        receiveBuilder = receiveBuilder.match(type, apply);
    }

    public PartialFunction<Object, BoxedUnit> receive() {
        return receiveBuilder.build();
    }

    public UntypedActorContext getContext() {
        return (UntypedActorContext) context();
    }
}
