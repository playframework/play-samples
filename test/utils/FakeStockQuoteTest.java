package utils;

import org.junit.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.*;

public class FakeStockQuoteTest {

    @Test
    public void fakeStockPriceShouldBePlusOrMinusFivePercentOfTheOldPrice() {
        FakeStockQuote stockQuote = new FakeStockQuote();
        Double origPrice = new Random().nextDouble();
        Double newPrice = stockQuote.newPrice(origPrice);
        assertThat(newPrice).isGreaterThan(origPrice - (origPrice * 0.05));
        assertThat(newPrice).isLessThan(origPrice + (origPrice * 0.05));
    }

}
