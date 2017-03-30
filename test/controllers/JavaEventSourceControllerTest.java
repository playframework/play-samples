package controllers;

import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;

import static org.junit.Assert.assertEquals;
import static play.test.Helpers.*;

public class JavaEventSourceControllerTest extends WithApplication {

    @Test
    public void testClock() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .host("localhost")
                .method(GET)
                .uri("/java/eventSource/liveClock");

        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

}
