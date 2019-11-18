package actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;
import stocks.Stock;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * This actor contains a set of stocks internally that may be used by
 * all websocket clients.
 */
public final class StocksActor {
  private StocksActor() {}

  public static class Stocks {
    final Set<Stock> stocks;

    public Stocks(Set<Stock> stocks) {
      this.stocks = requireNonNull(stocks);
    }
  }

  public static final class GetStocks {
    final Set<String> symbols;
    final ActorRef<Stocks> replyTo;

    public GetStocks(Set<String> symbols, ActorRef<Stocks> replyTo) {
      this.symbols = requireNonNull(symbols);
      this.replyTo = requireNonNull(replyTo);
    }

    @Override
    public String toString() {
      return "GetStocks(" + symbols + ")";
    }
  }

  public static Behavior<GetStocks> create() {
    Map<String, Stock> stocksMap = new HashMap<>();
    return Behaviors.logMessages(
        Behaviors
            .receive(GetStocks.class)
            .onMessage(GetStocks.class, getStocks -> {
              Set<Stock> stocks = getStocks.symbols.stream()
                  .map(symbol -> stocksMap.compute(symbol, (k, v) -> new Stock(k)))
                  .collect(Collectors.toSet());
              getStocks.replyTo.tell(new Stocks(stocks));
              return Behaviors.same();
            })
            .build()
    );
  }
}
