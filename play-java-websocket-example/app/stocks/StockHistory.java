package stocks;

import java.util.List;

import static java.util.Objects.requireNonNull;

/** A JSON presentation class for stock history. */
public class StockHistory {
    private final String symbol;
    private final List<Double> prices;

    public StockHistory(String symbol, List<Double> prices) {
        this.symbol = requireNonNull(symbol);
        this.prices = requireNonNull(prices);
    }

    public String getType() {
        return "stockhistory";
    }

    public String getSymbol() {
        return symbol;
    }

    public List<Double> getHistory() {
        return prices;
    }
}
