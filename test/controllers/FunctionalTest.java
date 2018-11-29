package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import play.libs.Json;
import play.shaded.ahc.org.asynchttpclient.AsyncHttpClient;
import play.shaded.ahc.org.asynchttpclient.AsyncHttpClientConfig;
import play.shaded.ahc.org.asynchttpclient.DefaultAsyncHttpClient;
import play.shaded.ahc.org.asynchttpclient.DefaultAsyncHttpClientConfig;
import play.shaded.ahc.org.asynchttpclient.netty.ws.NettyWebSocket;
import play.shaded.ahc.org.asynchttpclient.ws.WebSocket;
import play.test.TestServer;

import java.util.Collections;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.awaitility.Awaitility.await;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

public class FunctionalTest {


    @Test
    public void testRejectWebSocket() {
        TestServer server = testServer(37117);
        running(server, () -> {
            try {
                AsyncHttpClientConfig config = new DefaultAsyncHttpClientConfig.Builder().setMaxRequestRetry(0).build();
                AsyncHttpClient client = new DefaultAsyncHttpClient(config);
                WebSocketClient webSocketClient = new WebSocketClient(client);

                try {
                    String serverURL = "ws://localhost:37117/ws";
                    WebSocketClient.LoggingListener listener = new WebSocketClient.LoggingListener(message -> {});
                    CompletableFuture<NettyWebSocket> completionStage = webSocketClient.call(serverURL, serverURL, listener);
                    await().until(completionStage::isDone);
                    assertThat(completionStage.get())
                        .isNull();
                } finally {
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
                    ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(10);
                    WebSocketClient.LoggingListener listener = new WebSocketClient.LoggingListener((message) -> {
                        try {
                            queue.put(message);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                    CompletableFuture<NettyWebSocket> completionStage = webSocketClient.call(serverURL, serverURL, listener);

                    await().until(completionStage::isDone);
                    WebSocket websocket = completionStage.get();
                    await().until(() -> websocket.isOpen() && queue.peek() != null);
                    String input = queue.take();

                    JsonNode json = Json.parse(input);
                    String symbol = json.get("symbol").asText();
                    assertThat(Collections.singletonList(symbol)).isSubsetOf("AAPL", "GOOG", "ORCL");
                } finally {
                    client.close();
                }
            } catch (Exception e) {
                fail("Unexpected exception", e);
            }
        });
    }
}
