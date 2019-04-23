package controllers;

import org.junit.Test;
import play.Application;
import play.filters.csrf.*;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Result;
import play.test.WithApplication;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.RequestBuilder;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

public class $model;format="Camel"$ControllerTest extends WithApplication {

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

    protected RequestBuilder addCsrfToken(RequestBuilder requestBuilder) {
        final CSRFFilter csrfFilter = app.injector().instanceOf(CSRFFilter.class);
        final CSRFConfig csrfConfig = app.injector().instanceOf(CSRFConfigProvider.class).get();
        final String token = csrfFilter.tokenProvider().generateToken();

        requestBuilder.tag(CSRF.Token\$.MODULE\$.NameRequestTag(), csrfConfig.tokenName());
        requestBuilder.tag(CSRF.Token\$.MODULE\$.RequestTag(), token);
        requestBuilder.header(csrfConfig.headerName(), token);

        return requestBuilder;
    }

    @Test
    public void test$model;format="Camel"$Get() {
        RequestBuilder request = new RequestBuilder()
                .method(GET)
                .uri("/$model;format="camel"$");

        Result result = route(app, request);
        assertEquals(OK, result.status());
    }


    @Test
    public void test$model;format="Camel"$Post() {
        HashMap<String, String> formData = new HashMap<>();
        formData.put("name", "play");
        formData.put("age", "4");
        RequestBuilder request = addCsrfToken(new RequestBuilder()
                .header("Host", "localhost")
                .method(POST)
                .bodyForm(formData)
                .uri("/$model;format="camel"$"));

        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());
    }

}
