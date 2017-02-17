package controllers;

import org.junit.Test;
import play.Application;
import play.mvc.Http;
import play.mvc.Result;

import static org.junit.Assert.assertEquals;
import static play.test.Helpers.*;

public class JavaEventSourceControllerTest {

    final Application app = fakeApplication();

    @Test
    public void testClock() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/java/eventSource/liveClock");

        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

}
