package actors;

import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.actor.UntypedActor;
import java.util.concurrent.TimeUnit;
import java.util.Deque;
import java.util.HashSet;
import scala.concurrent.duration.Duration;
import utils.FakeStockQuote;
import utils.StockQuote;

/**
 * There is one StockActor per stock symbol.  The StockActor maintains a list of users watching the stock and the stock
 * values.  Each StockActor updates a rolling dataset of randomly generated stock values.
 */
public class StockActor extends UntypedActor {

    final String symbol;

    final StockQuote stockQuote;

    final HashSet<ActorRef> watchers = new HashSet<ActorRef>();

    final Deque<Double> stockHistory = FakeStockQuote.history(50);

    public StockActor(String symbol) {
        this.symbol = symbol;
        this.stockQuote = new FakeStockQuote();
    }

    public StockActor(String symbol, StockQuote stockQuote) {
        this.symbol = symbol;
        this.stockQuote = stockQuote;
    }

    // fetch the latest stock value every 75ms
    Cancellable stockTick = getContext().system().scheduler().schedule(
        Duration.Zero(), Duration.create(75, TimeUnit.MILLISECONDS),
        getSelf(), Stock.latest, getContext().dispatcher(), null);

    public void onReceive(Object message) {
        if (message instanceof Stock.Latest) {
            // add a new stock price to the history and drop the oldest
            Double newPrice = stockQuote.newPrice(stockHistory.peekLast());
            stockHistory.add(newPrice);
            stockHistory.remove();
            // notify watchers
            watchers.forEach(watcher -> watcher.tell(new Stock.Update(symbol, newPrice), getSelf()));

        } else if (message instanceof Stock.Watch) {
            // send the stock history to the user
            getSender().tell(new Stock.History(symbol, stockHistory), getSelf());
            // add the watcher to the list
            watchers.add(getSender());

        } else if (message instanceof Stock.Unwatch) {
            watchers.remove(getSender());
            if (watchers.isEmpty()) {
                stockTick.cancel();
                getContext().stop(getSelf());
            }
        }
    }
}
