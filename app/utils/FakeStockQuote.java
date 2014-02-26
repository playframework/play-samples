package utils;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Random;

/**
 * Randomly generated prices.
 */
public class FakeStockQuote implements StockQuote {

    private final Random random = new Random();

    /**
     * Creates a randomly generated price based on the previous price.
     */
    public Double newPrice(Double lastPrice) {
        return lastPrice * (0.95  + (0.1 * random.nextDouble()));
    }

    /**
     * Creates an initial history of random prices.
     */
    public static Deque<Double> history(int length) {
        FakeStockQuote stockQuote = new FakeStockQuote();
        Deque<Double> prices = new LinkedList<Double>();
        prices.add((new Random()).nextDouble() * 800);
        for (int i = 1; i < length; i++) {
            prices.add(stockQuote.newPrice(prices.peekLast()));
        }
        return prices;
    }
}
