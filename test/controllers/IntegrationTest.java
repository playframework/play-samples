package controllers;

import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;
import v1.post.PostData;
import v1.post.PostRepository;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static play.test.Helpers.*;

public class IntegrationTest extends WithApplication {

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

    @Test
    public void testIndex() {
        PostRepository repository = app.injector().instanceOf(PostRepository.class);
        repository.create(new PostData("title", "body"));

        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/v1/post");

        Result result = route(app, request);
        final String body = contentAsString(result);
        assertThat(body, containsString("body"));
    }

}
