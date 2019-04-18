package stocks;

public interface StockQuoteGenerator {
    StockQuote newQuote(StockQuote last);

    StockQuote seed();
}
