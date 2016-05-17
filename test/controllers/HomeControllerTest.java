package controllers;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.japi.Pair;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Source;
import akka.stream.testkit.TestPublisher;
import akka.stream.testkit.TestSubscriber;
import akka.stream.testkit.javadsl.TestSink;
import akka.stream.testkit.javadsl.TestSource;
import akka.testkit.JavaTestKit;
import akka.testkit.TestProbe;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import play.libs.Json;

/**
 * Tests the home controller.
 */
public class HomeControllerTest {

    static ActorSystem system;
    static Materializer materializer;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create("test");
        materializer = ActorMaterializer.create(system);
    }

    @AfterClass
    public static void teardown() {
        JavaTestKit.shutdownActorSystem(system);
        materializer = null;
        system = null;
    }

    @Test
    public void testCreateWebSocketFlow() {
        // Injected dependencies to create the controller...
        final TestProbe stocksActorProbe = new TestProbe(system, "stocksActor");
        final TestProbe userParentActorProbe = new TestProbe(system, "userParentActor");

        // Create the controller without having to create a play app...
        final HomeController controller = new HomeController(system,
                materializer,
                stocksActorProbe.ref(),
                userParentActorProbe.ref());

        // input for creating a flow...
        final TestProbe userActorProbe = new TestProbe(system, "userActor");
        TestPublisher.Probe<JsonNode> publisher = TestPublisher.probe(0, system);

        // method under test
        final Flow<JsonNode, JsonNode, NotUsed> flowUnderTest = controller.createWebSocketFlow(publisher, userActorProbe.ref());

        Source<JsonNode, TestPublisher.Probe<JsonNode>> sourceProbe = TestSource.probe(system);

        // create a test source and sink around the flow
        final Pair<TestPublisher.Probe<JsonNode>, TestSubscriber.Probe<JsonNode>> pair = sourceProbe
                .via(flowUnderTest)
                .toMat(TestSink.probe(system), Keep.both())
                .run(materializer);

        final TestPublisher.Probe<JsonNode> pub = pair.first();
        final TestSubscriber.Probe<JsonNode> sub = pair.second();

        // check that a message sent in will come out the other end
        final ObjectNode jsvalue = Json.newObject().put("herp", "derp");
        sub.request(1);
        publisher.sendNext(jsvalue);
        sub.expectNext(jsvalue);
    }
}
