package actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import play.libs.Akka;
import java.util.Optional;

public class StocksActor extends UntypedActor {

    private static class LazyStocksActor {
        public static final ActorRef ref = Akka.system().actorOf(Props.create(StocksActor.class));
    }

    public static ActorRef stocksActor() {
        return LazyStocksActor.ref;
    }

    public StocksActor() {}

    public void onReceive(Object message) {
        if (message instanceof Stock.Watch) {
            Stock.Watch watch = (Stock.Watch) message;
            String symbol = watch.symbol;
            // get or create the StockActor for the symbol and forward this message
            ActorRef child = getContext().getChild(symbol);
            if (child == null) {
                child = getContext().actorOf(Props.create(StockActor.class, symbol), symbol);
            }
            child.forward(watch, getContext());

        } else if (message instanceof Stock.Unwatch) {
            Stock.Unwatch unwatch = (Stock.Unwatch) message;
            Optional<String> optionalSymbol = unwatch.symbol;
            if (optionalSymbol.isPresent()) {
                // if there is a StockActor for the symbol forward this message
                String symbol = optionalSymbol.get();
                ActorRef child = getContext().getChild(symbol);
                if (child != null) {
                    child.forward(unwatch, getContext());
                }
            } else { // no symbol is specified, forward to everyone
                getContext().getChildren().forEach(child -> child.forward(unwatch, getContext()));
            }
        }
    }
}
