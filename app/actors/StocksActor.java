package actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import java.util.Optional;
import play.libs.Akka;
import utils.LambdaActor;

import static utils.Functions.consumer;
import static utils.Functions.supplier;

public class StocksActor extends LambdaActor {

    private static class LazyStocksActor {
        public static final ActorRef ref = Akka.system().actorOf(Props.create(StocksActor.class));
    }

    public static ActorRef stocksActor() {
        return LazyStocksActor.ref;
    }

    public StocksActor() {
        receive(Stock.Watch.class, watch -> {
            String symbol = watch.symbol;
            // get or create the StockActor for the symbol and forward this message
            context().child(symbol).getOrElse(supplier(
                () -> context().actorOf(Props.create(StockActor.class, symbol), symbol)
            )).forward(watch, context());
        });

        receive(Stock.Unwatch.class, unwatch -> {
            Optional<String> optionalSymbol = unwatch.symbol;
            if (optionalSymbol.isPresent()) {
                // if there is a StockActor for the symbol forward this message
                context().child(optionalSymbol.get()).foreach(consumer(child -> child.forward(unwatch, context())));
            } else { // no symbol is specified, forward to everyone
                context().children().foreach(consumer(child -> child.forward(unwatch, context())));
            }
        });
    }
}
