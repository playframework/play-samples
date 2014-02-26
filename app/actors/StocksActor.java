package actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import java.util.Collections;
import java.util.Optional;
import play.libs.Akka;
import utils.LambdaActor;

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
            Optional.ofNullable(getContext().getChild(symbol)).orElseGet(
                () -> context().actorOf(Props.create(StockActor.class, symbol), symbol)
            ).forward(watch, context());
        });

        receive(Stock.Unwatch.class, unwatch -> {
            // forward this message to the associated StockActor, or otherwise to everyone
            unwatch.symbol
                   .map(getContext()::getChild)
                   .<Iterable<ActorRef>>map(Collections::singletonList)
                   .orElse(getContext().getChildren())
                   .forEach(child -> child.forward(unwatch, context()));
        });
    }
}
