package actors;

import akka.actor.ActorRef;
import akka.actor.Cancellable;
import java.util.concurrent.TimeUnit;
import java.util.Deque;
import java.util.HashSet;
import scala.concurrent.duration.Duration;
import utils.FakeStockQuote;
import utils.StockQuote;
import utils.LambdaActor;

/**
 * There is one StockActor per stock symbol.  The StockActor maintains a list of users watching the stock and the stock
 * values.  Each StockActor updates a rolling dataset of randomly generated stock values.
 */
public class StockActor extends LambdaActor {

    final String symbol;

    final StockQuote stockQuote;

    final HashSet<ActorRef> watchers = new HashSet<ActorRef>();

    final Deque<Double> stockHistory = FakeStockQuote.history(50);

    // fetch the latest stock value every 75ms
    Cancellable stockTick = context().system().scheduler().schedule(
        Duration.Zero(), Duration.create(75, TimeUnit.MILLISECONDS),
        self(), Stock.latest, context().dispatcher(), null);

    public StockActor(String symbol) {
        this(symbol, new FakeStockQuote());
    }

    public StockActor(String symbol, StockQuote stockQuote) {
        this.symbol = symbol;
        this.stockQuote = stockQuote;

        receive(Stock.Latest.class, latest -> {
            // add a new stock price to the history and drop the oldest
            Double newPrice = stockQuote.newPrice(stockHistory.peekLast());
            stockHistory.add(newPrice);
            stockHistory.remove();
            // notify watchers
            watchers.forEach(watcher -> watcher.tell(new Stock.Update(symbol, newPrice), self()));
        });

        receive(Stock.Watch.class, watch -> {
            // send the stock history to the user
            sender().tell(new Stock.History(symbol, stockHistory), self());
            // add the watcher to the list
            watchers.add(sender());
        });

        receive(Stock.Unwatch.class, unwatch -> {
            watchers.remove(sender());
            if (watchers.isEmpty()) {
                stockTick.cancel();
                context().stop(self());
            }
        });
    }
}
