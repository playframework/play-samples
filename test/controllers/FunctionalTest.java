package controllers;

import play.shaded.ahc.org.asynchttpclient.AsyncHttpClient;
import play.shaded.ahc.org.asynchttpclient.AsyncHttpClientConfig;
import play.shaded.ahc.org.asynchttpclient.DefaultAsyncHttpClient;
import play.shaded.ahc.org.asynchttpclient.DefaultAsyncHttpClientConfig;
import play.shaded.ahc.org.asynchttpclient.ws.WebSocket;
import org.junit.Test;
import play.test.TestServer;

import java.util.concurrent.CompletableFuture;

import static com.jayway.awaitility.Awaitility.await;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

public class FunctionalTest {

    @Test
    public void testRejectWebSocket() {
        TestServer server = testServer(31337);
        running(server, () -> {
            try {
                AsyncHttpClientConfig config = new DefaultAsyncHttpClientConfig.Builder().setMaxRequestRetry(0).build();
                AsyncHttpClient client = new DefaultAsyncHttpClient(config);
                WebSocketClient webSocketClient = new WebSocketClient(client);

                try {
                    // localhost:31337 is not an acceptable origin to the server, so this will fail...
                    String serverURL = "ws://localhost:31337/ws";
                    WebSocketClient.LoggingListener listener = new WebSocketClient.LoggingListener();
                    CompletableFuture<WebSocket> completionStage = webSocketClient.call(serverURL, listener);

                    await().until(() -> {
                        assertThat(completionStage)
                                .hasFailedWithThrowableThat()
                                .hasMessageContaining("Invalid Status Code 403");
                    });
                } finally {
                    //noinspection ThrowFromFinallyBlock
                    client.close();
                }
            } catch (Exception e) {
                fail("Unexpected exception", e);
            }
        });
    }

    @Test
    public void testAcceptWebSocket() {
        TestServer server = testServer(19001);
        running(server, () -> {
            try {
                AsyncHttpClientConfig config = new DefaultAsyncHttpClientConfig.Builder().setMaxRequestRetry(0).build();
                AsyncHttpClient client = new DefaultAsyncHttpClient(config);
                WebSocketClient webSocketClient = new WebSocketClient(client);

                try {
                    String serverURL = "ws://localhost:19001/ws";
                    WebSocketClient.LoggingListener listener = new WebSocketClient.LoggingListener();
                    CompletableFuture<WebSocket> completionStage = webSocketClient.call(serverURL, listener);

                    await().until(() -> {
                        assertThat(completionStage).isDone();
                    });
                } finally {
                    //noinspection ThrowFromFinallyBlock
                    client.close();
                }
            } catch (Exception e) {
                fail("Unexpected exception", e);
            }
        });
    }
}
