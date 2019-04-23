package stocks;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import static java.util.Objects.requireNonNull;

/** A JSON presentation class for stock updates. */
public class StockUpdate {
    private final String symbol;
    private final Double price;

    public StockUpdate(String symbol, Double price) {
        this.symbol = requireNonNull(symbol);
        this.price = requireNonNull(price);
    }

    public String getType() {
        return "stockupdate";
    }

    public Double getPrice() {
        return price;
    }

    public String getSymbol() {
        return symbol;
    }
}
