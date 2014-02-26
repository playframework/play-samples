package actors;

import com.fasterxml.jackson.databind.JsonNode;
import play.mvc.WebSocket;

/**
 * A stub class that looks like WebSocket.Out to the rest of the system, and
 * returns the actual results of the test to check against our expectations.
 */
public class StubOut implements WebSocket.Out<JsonNode> {
  public JsonNode actual;

  public void write(JsonNode node) {
    actual = node;
  }

  public void close() {}
}
