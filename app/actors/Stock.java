package actors;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

public class Stock {
    public static final class Latest {
        public Latest() {
        }
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
        public final Double[] history;

        public History(String symbol, Double[] history) {
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
        private final String symbol;

        public Optional<String> symbol() {
            return Optional.ofNullable(symbol);
        }

        public Unwatch(String symbol) {
            this.symbol = symbol;
        }
    }
}
