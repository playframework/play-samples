package actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.util.Collections;
import java.util.Optional;

/**
 *
 */
public class StocksActor extends UntypedActor {

    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    @Override
    public void onReceive(Object message) throws Exception {

        if (message instanceof Stock.Watch) {
            Stock.Watch watch = (Stock.Watch) message;
            String symbol = watch.symbol;
            // get or create the StockActor for the symbol and forward this message
            Optional.ofNullable(getContext().getChild(symbol)).orElseGet(() -> {
                        final Props props = Props.create(StockActor.class, symbol);
                        return context().actorOf(props, symbol);
                    }
            ).forward(watch, context());
        }

        if (message instanceof Stock.Unwatch) {
            Stock.Unwatch unwatch = (Stock.Unwatch) message;
            // forward this message to the associated StockActor, or otherwise to everyone
            unwatch.symbol()
                    .map(getContext()::getChild)
                    .<Iterable<ActorRef>>map(Collections::singletonList)
                    .orElse(getContext().getChildren())
                    .forEach(child -> child.forward(unwatch, context()));
        }
    }
}
