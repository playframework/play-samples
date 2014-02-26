package actors;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import akka.testkit.TestActorRef;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;
import static org.fest.assertions.Assertions.assertThat;

public class UserActorTest {

    static StubOut out;
    static ActorSystem system;
    static TestActorRef<UserActor> userActor;

    @BeforeClass
    public static void setup() {
        out = new StubOut();
        system = ActorSystem.create("UserActorTest");
        Props props = Props.create(UserActor.class, out);
        userActor = TestActorRef.create(system, props, "userActor");
    }

    @AfterClass
    public static void teardown() {
        JavaTestKit.shutdownActorSystem(system);
        system = null; userActor = null; out = null;
    }

    @Test
    public void userActorShouldSendStockUpdate() {
        running(fakeApplication(), () -> {
            String symbol = "ABC";
            double price = 123;

            // send off the stock update ...
            userActor.receive(new Stock.Update(symbol, price));

            // ... and expect it to be a JSON node
            assertThat(out.actual.get("type").asText()).isEqualTo("stockupdate");
            assertThat(out.actual.get("symbol").asText()).isEqualTo(symbol);
            assertThat(out.actual.get("price").asDouble()).isEqualTo(price);
        });
    }

    @Test
    public void userActorShouldSendStockHistory() {
        running(fakeApplication(), () -> {
            String symbol = "ABC";
            Deque<Double> history = new LinkedList<Double>(Arrays.asList(0.1, 1.0));

            // send off the stock history ...
            userActor.receive(new Stock.History(symbol, history));

            // ... and expect it to be a JSON node
            assertThat(out.actual.get("type").asText()).isEqualTo("stockhistory");
            assertThat(out.actual.get("symbol").asText()).isEqualTo(symbol);
            assertThat(out.actual.get("history").get(0).asDouble()).isEqualTo(history.getFirst());
            assertThat(out.actual.get("history").get(1).asDouble()).isEqualTo(history.getLast());
        });
    }
}
