package actors;

import akka.actor.ActorRef;
import akka.actor.Cancellable;
import java.util.concurrent.TimeUnit;
import java.util.Deque;
import java.util.HashSet;
import java.util.Optional;
import scala.concurrent.duration.Duration;
import utils.FakeStockQuote;
import utils.LambdaActor;
import utils.StockQuote;

/**
 * There is one StockActor per stock symbol.  The StockActor maintains a list of users watching the stock and the stock
 * values.  Each StockActor updates a rolling dataset of randomly generated stock values.
 */
public class StockActor extends LambdaActor {

    final HashSet<ActorRef> watchers = new HashSet<ActorRef>();

    final Deque<Double> stockHistory = FakeStockQuote.history(50);

    public StockActor(String symbol) {
        this(symbol, new FakeStockQuote(), true);
    }

    public StockActor(String symbol, StockQuote stockQuote, boolean tick) {
        Optional<Cancellable> stockTick = tick ? Optional.of(scheduleTick()) : Optional.empty();

        receive(Stock.Latest.class, latest -> {
            // add a new stock price to the history and drop the oldest
            Double newPrice = stockQuote.newPrice(stockHistory.peekLast());
            stockHistory.add(newPrice);
            stockHistory.remove();
            // notify watchers
            watchers.forEach(watcher -> watcher.tell(new Stock.Update(symbol, newPrice), self()));
        });

        receive(Stock.Watch.class, watch -> {
            // reply with the stock history, and add the sender as a watcher
            sender().tell(new Stock.History(symbol, stockHistory), self());
            watchers.add(sender());
        });

        receive(Stock.Unwatch.class, unwatch -> {
            watchers.remove(sender());
            if (watchers.isEmpty()) {
                stockTick.ifPresent(Cancellable::cancel);
                context().stop(self());
            }
        });
    }

    private Cancellable scheduleTick() {
        return context().system().scheduler().schedule(
            Duration.Zero(), Duration.create(75, TimeUnit.MILLISECONDS),
            self(), Stock.latest, context().dispatcher(), null);
    }
}
