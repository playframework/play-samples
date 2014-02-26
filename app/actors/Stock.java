package actors;

import java.util.Deque;
import java.util.Optional;

public class Stock {
    public static final class Latest {
        public Latest() {}
    }

    public static final Latest latest = new Latest();

    public static final class Update {
        public final String symbol;
        public final Double price;

        public Update(String symbol, Double price) {
            this.symbol = symbol;
            this.price = price;
        }
    }

    public static final class History {
        public final String symbol;
        public final Deque<Double> history;

        public History(String symbol, Deque<Double> history) {
            this.symbol = symbol;
            this.history = history;
        }
    }

    public static final class Watch {
        public final String symbol;

        public Watch(String symbol) {
            this.symbol = symbol;
        }
    }

    public static final class Unwatch {
        public final Optional<String> symbol;

        public Unwatch(Optional<String> symbol) {
            this.symbol = symbol;
        }
    }
}
