package actors;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.Creator;
import akka.testkit.JavaTestKit;
import akka.testkit.TestActorRef;
import akka.testkit.TestProbe;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import play.Configuration;
import play.Environment;
import scala.concurrent.duration.FiniteDuration;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;

import static org.assertj.core.api.Assertions.*;

import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

public class UserActorTest {
    // http://doc.akka.io/japi/akka/2.4.4/akka/testkit/package-summary.html
    //

    static TestProbe out;
    static TestProbe stocksActor;
    static ActorSystem system;
    static Configuration configuration;
    static TestActorRef<UserActor> userActor;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create("test");
        out = new TestProbe(system, "out");
        stocksActor = new TestProbe(system, "stocksActor");
        configuration = Configuration.load(Environment.simple());
        Props props = Props.create(UserActor.class, (Creator<UserActor>) () ->
                new UserActor(out.ref(), stocksActor.ref(), configuration)
        );
        userActor = TestActorRef.create(system, props, "userActor");
    }

    @AfterClass
    public static void teardown() {
        JavaTestKit.shutdownActorSystem(system);
        system = null;
        userActor = null;
        stocksActor = null;
        out = null;
        configuration = null;
    }

    @Test
    public void userActorShouldSendStockUpdate() {
        running(fakeApplication(), () -> {
            String symbol = "ABC";
            double price = 123;

            // send off the stock update ...
            userActor.receive(new Stock.Update(symbol, price));

            final FiniteDuration duration = JavaTestKit.duration("1 second");
            JsonNode output = (JsonNode) out.receiveOne(duration);

            // ... and expect it to be a JSON node
            assertThat(output.get("type").asText()).isEqualTo("stockupdate");
            assertThat(output.get("symbol").asText()).isEqualTo(symbol);
            assertThat(output.get("price").asDouble()).isEqualTo(price);
        });
    }

    @Test
    public void userActorShouldSendStockHistory() {
        running(fakeApplication(), () -> {
            String symbol = "ABC";
            Deque<Double> history = new LinkedList<Double>(Arrays.asList(0.1, 1.0));

            // send off the stock history ...
            userActor.receive(new Stock.History(symbol, history));

            final FiniteDuration duration = JavaTestKit.duration("1 second");
            JsonNode output = (JsonNode) out.receiveOne(duration);

            // ... and expect it to be a JSON node
            assertThat(output.get("type").asText()).isEqualTo("stockhistory");
            assertThat(output.get("symbol").asText()).isEqualTo(symbol);
            assertThat(output.get("history").get(0).asDouble()).isEqualTo(history.getFirst());
            assertThat(output.get("history").get(1).asDouble()).isEqualTo(history.getLast());
        });
    }
}
