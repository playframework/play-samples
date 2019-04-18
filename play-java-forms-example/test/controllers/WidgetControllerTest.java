package controllers;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

/**
 * Tests form processing.
 *
 * https://www.playframework.com/documentation/latest/JavaFunctionalTest
 */
public class WidgetControllerTest extends WithApplication {

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

    @Test
    public void testIndex() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/");

        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testCreateWidget() {
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(POST)
                .bodyForm(ImmutableMap.of("name","widget 6", "price", "6"))
                .uri("/widgets");

        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
    }

}
