package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;

import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.Deque;
import java.util.HashSet;
import java.util.Optional;
import scala.concurrent.duration.Duration;
import utils.FakeStockQuote;
import utils.StockQuote;

/**
 * There is one StockActor per stock symbol.  The StockActor maintains a list of users watching the stock and the stock
 * values.  Each StockActor updates a rolling dataset of randomly generated stock values.
 */
public class StockActor extends AbstractActor {

    private final HashSet<ActorRef> watchers = new HashSet<ActorRef>();

    private final Deque<Double> stockHistory = FakeStockQuote.history(50);

    public StockActor(String symbol) {
        this(symbol, new FakeStockQuote(), true);
    }

    public StockActor(String symbol, StockQuote stockQuote, boolean tick) {
        Optional<Cancellable> stockTick = tick ? Optional.of(scheduleTick()) : Optional.empty();

        receive(ReceiveBuilder
            .match(Stock.Latest.class, latest -> {
                // add a new stock price to the history and drop the oldest
                Double newPrice = stockQuote.newPrice(stockHistory.peekLast());
                stockHistory.add(newPrice);
                stockHistory.remove();
                // notify watchers
                watchers.forEach(watcher -> watcher.tell(new Stock.Update(symbol, newPrice), self()));
            })
            .match(Stock.Watch.class, watch -> {
                // reply with the stock history, and add the sender as a watcher
                final Double[] clone = stockHistory.toArray(new Double[]{});
                sender().tell(new Stock.History(symbol, clone), self());
                watchers.add(sender());
            })
            .match(Stock.Unwatch.class, unwatch -> {
                watchers.remove(sender());
                if (watchers.isEmpty()) {
                    stockTick.ifPresent(Cancellable::cancel);
                    context().stop(self());
                }
            }).build());
    }

    private Cancellable scheduleTick() {
        return context().system().scheduler().schedule(
            Duration.Zero(), Duration.create(75, TimeUnit.MILLISECONDS),
            self(), Stock.latest, context().dispatcher(), null);
    }
}
