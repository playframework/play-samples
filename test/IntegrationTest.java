import org.junit.Test;
import play.libs.ws.WS;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithServer;

import java.util.concurrent.CompletionStage;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static play.test.Helpers.*;

/**
 * Integration testing that involves starting up an application or a server.
 * <p>
 * https://www.playframework.com/documentation/2.5.x/JavaFunctionalTest
 */
public class IntegrationTest extends WithServer {

    @Test
    public void testInServerThroughUrl() throws Exception {
        // Tests using a scoped WSClient to talk to the server through a port.
        try (WSClient ws = WS.newClient(this.testServer.port())) {
            CompletionStage<WSResponse> stage = ws.url("/").get();
            WSResponse response = stage.toCompletableFuture().get();
            String body = response.getBody();
            assertThat(body, containsString("Add Person"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInServerThroughApp() throws Exception {
        // Tests using the internal application available in the server.
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/");

        Result result = route(app, request);
        final String body = contentAsString(result);
        assertThat(body, containsString("Add Person"));
    }

}
