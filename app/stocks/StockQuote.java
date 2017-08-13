package stocks;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class StockQuote {
    public final String symbol;
    public final Double price;

    public StockQuote(String symbol, Double price) {
        this.symbol = requireNonNull(symbol);
        this.price = requireNonNull(price);
    }
}
