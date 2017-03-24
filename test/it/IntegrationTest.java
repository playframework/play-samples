package it;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.AbstractModule;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;
import v1.post.PostData;
import v1.post.PostRepository;
import v1.post.PostResource;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static play.test.Helpers.*;

public class IntegrationTest extends WithApplication {

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

    @Test
    public void testList() {
        PostRepository repository = app.injector().instanceOf(PostRepository.class);
        repository.create(new PostData("title", "body"));

        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/v1/posts");

        Result result = route(app, request);
        final String body = contentAsString(result);
        assertThat(body, containsString("body"));
    }

    @Test
    public void testTimeoutOnUpdate() {
        PostRepository repository = app.injector().instanceOf(PostRepository.class);
        repository.create(new PostData("title", "body"));

        JsonNode json = Json.toJson(new PostResource("1", "http://localhost:9000/v1/posts/1", "some title", "somebody"));

        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .bodyJson(json)
                .uri("/v1/posts/1");

        Result result = route(app, request);
        assertThat(result.status(), equalTo(GATEWAY_TIMEOUT));
    }

    @Test
    public void testCircuitBreakerOnShow() {
        PostRepository repository = app.injector().instanceOf(PostRepository.class);
        repository.create(new PostData("title", "body"));

        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/v1/posts/1");

        Result result = route(app, request);
        assertThat(result.status(), equalTo(SERVICE_UNAVAILABLE));
    }


}
