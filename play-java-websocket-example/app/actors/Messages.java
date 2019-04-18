package actors;

import stocks.Stock;

import java.util.Set;

import static java.util.Objects.requireNonNull;

public final class Messages {

    public static final class WatchStocks {
        final Set<String> symbols;

        public WatchStocks(Set<String> symbols) {
            this.symbols = requireNonNull(symbols);
        }

        @Override
        public String toString() {
            return "WatchStocks(" + symbols.toString() + ")";
        }
    }

    public static final class UnwatchStocks {
        final Set<String> symbols;

        public UnwatchStocks(Set<String> symbols) {
            this.symbols = requireNonNull(symbols);
        }

        @Override
        public String toString() {
            return "UnwatchStocks(" + symbols.toString() + ")";
        }
    }

    public static class Stocks {
        final Set<Stock> stocks;

        public Stocks(Set<Stock> stocks) {
            this.stocks = requireNonNull(stocks);
        }
    }
}

