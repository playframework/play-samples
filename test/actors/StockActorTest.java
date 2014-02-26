package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import akka.testkit.TestActorRef;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import utils.StockQuote;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;
import static org.fest.assertions.Assertions.assertThat;

public class StockActorTest {

    static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create("UserActorTest");
    }

    @AfterClass
    public static void teardown() {
        JavaTestKit.shutdownActorSystem(system);
        system = null;
    }

    public static class FixedStockQuote implements StockQuote {
        Double price;

        public FixedStockQuote(Double price) {
            this.price = price;
        }

        public Double newPrice(Double lastPrice) {
            return price;
        }
    }

    @Test
    public void stockActorShouldNotifyWatchers() {
        running(fakeApplication(), () -> new JavaTestKit(system) {{
            String symbol = "ABC";
            double price = 1234;

            Props props = Props.create(StockActor.class, symbol, new FixedStockQuote(price), /*tick = */ false);
            ActorRef stockActor = system.actorOf(props, "stockActor");

            // receive Stock.History when adding a watcher with Stock.Watch
            stockActor.tell(new Stock.Watch(symbol), getRef());
            Stock.History history = expectMsgClass(Stock.History.class);

            // receive Stock.Update on Stock.Latest tick
            stockActor.tell(Stock.latest, getRef());
            Stock.Update update = expectMsgClass(Stock.Update.class);
            assertThat(update.symbol).isEqualTo(symbol);
            assertThat(update.price).isEqualTo(price);
        }});
    }
}
