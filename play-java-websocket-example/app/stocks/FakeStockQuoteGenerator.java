package stocks;

import java.util.concurrent.ThreadLocalRandom;

public class FakeStockQuoteGenerator implements StockQuoteGenerator {

    private final String symbol;

    public FakeStockQuoteGenerator(String symbol) {
        this.symbol = symbol;
    }

    private Double random() {
        return ThreadLocalRandom.current().nextDouble();
    }

    @Override
    public StockQuote newQuote(StockQuote last) {
        return new StockQuote(last.symbol, last.price * (0.95 + (0.1 * random())));
    }

    @Override
    public StockQuote seed() {
        return new StockQuote(symbol, random() * 800);
    }
}
