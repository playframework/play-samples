package controllers;

import play.shaded.ahc.org.asynchttpclient.AsyncHttpClient;
import play.shaded.ahc.org.asynchttpclient.DefaultAsyncHttpClient;
import play.shaded.ahc.org.asynchttpclient.ws.WebSocket;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import play.test.WithServer;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.OK;

/**
 * Limited functional testing to ensure health checks of build
 */
public class HomeControllerTest extends WithServer {

    private AsyncHttpClient asyncHttpClient;

    @Before
    public void setUp() {
        asyncHttpClient = new DefaultAsyncHttpClient();
    }

    @After
    public void tearDown() throws IOException {
        asyncHttpClient.close();
    }

    // Functional test to run through the server and check the page comes ups
    @Test
    public void testInServer() throws Exception {
        String url = "http://localhost:" + this.testServer.port() + "/";
        try (WSClient ws = play.test.WSTestClient.newClient(this.testServer.port())) {
            CompletionStage<WSResponse> stage = ws.url(url).get();
            WSResponse response = stage.toCompletableFuture().get();
            assertEquals(OK, response.getStatus());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Functional test to check websocket comes up
    @Test
    public void testWebsocket() throws Exception {
        String serverURL = "ws://localhost:" + this.testServer.port() + "/chat";
        String origin = serverURL;

        WebSocketClient webSocketClient = new WebSocketClient(asyncHttpClient);
        WebSocketClient.LoggingListener listener = new WebSocketClient.LoggingListener();
        CompletableFuture<WebSocket> future = webSocketClient.call(serverURL, origin, listener);
        await().until(() -> assertThat(future).isDone());
        assertThat(future).isCompletedWithValueMatching(result -> result.isOpen());
    }


}