package stocks;

import akka.NotUsed;
import akka.japi.Pair;
import akka.japi.function.Function;
import akka.stream.ThrottleMode;
import akka.stream.javadsl.Source;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * A stock is a source of stock quotes and a symbol.
 */
public class Stock {
    public final String symbol;

    private final StockQuoteGenerator stockQuoteGenerator;

    private final Source<StockQuote, NotUsed> source;

    private static final Duration duration = Duration.of(75, ChronoUnit.MILLIS);

    public Stock(String symbol) {
        this.symbol = requireNonNull(symbol);
        stockQuoteGenerator = new FakeStockQuoteGenerator(symbol);
        source = Source.unfold(stockQuoteGenerator.seed(), (Function<StockQuote, Optional<Pair<StockQuote, StockQuote>>>) last -> {
            StockQuote next = stockQuoteGenerator.newQuote(last);
            return Optional.of(Pair.apply(next, next));
        });
    }

    /**
     * Returns a source of stock history, containing a single element.
     */
    public Source<StockHistory, NotUsed> history(int n) {
        return source.grouped(n)
                .map(quotes -> new StockHistory(symbol, quotes.stream().map(sq -> sq.price).collect(Collectors.toList())))
                .take(1);
    }

    /**
     * Provides a source that returns a stock quote every 75 milliseconds.
     */
    public Source<StockUpdate, NotUsed> update() {
        return source.throttle(1, duration, 1, ThrottleMode.shaping())
                .map(sq -> new StockUpdate(sq.symbol, sq.price));
    }

    @Override
    public String toString() {
        return "Stock(" + symbol + ")";
    }
}
