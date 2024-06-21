package it;

import com.fasterxml.jackson.databind.JsonNode;
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

import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;
import static play.test.Helpers.*;

public class IntegrationTest extends WithApplication {

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

    @Test
    public void testList() {
        PostRepository repository = app.injector().instanceOf(PostRepository.class);
        repository.create(new PostData("title-of-post-123", "body-123"));

        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/v1/posts");

        Result result = route(app, request);

        assertEquals(200, result.status());

        JsonNode listOfPosts = contentAsJson(result);
        Optional<PostResource> post = findPostByTitle(listOfPosts, "title-of-post-123");
        assertTrue(post.isPresent());
    }

    private Optional<PostResource> findPostByTitle(JsonNode listOfPosts, String postTitle) {
        Iterator<JsonNode> elements = listOfPosts.elements();
        // spliterator dance to build a Stream from an Iterator
        return StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(
                elements,
                Spliterator.ORDERED),
            false)
            .map(jsonNode -> Json.fromJson(jsonNode, PostResource.class))
            .filter(p -> {
                return p.getTitle().equals(postTitle);
            })
            .findFirst();
    }

    private JsonNode contentAsJson(Result result) {
        final String responseBody = contentAsString(result);
        return Json.parse(responseBody);
    }

    @Test
    public void testListWithTrailingSlashIsUnknown() {
        PostRepository repository = app.injector().instanceOf(PostRepository.class);
        repository.create(new PostData("title-of-another-post", "body-456"));

        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/v1/posts/");

        Result result = route(app, request);
        assertEquals(404, result.status());
    }

    @Test
    public void testTimeoutOnUpdate() {
        PostRepository repository = app.injector().instanceOf(PostRepository.class);
        repository.create(new PostData("title-testTimeoutOnUpdate", "body-testTimeoutOnUpdate"));

        JsonNode json = Json.toJson(new PostResource("1", "http://localhost:9000/v1/posts/1", "some title", "somebody"));

        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(PUT)
                .bodyJson(json)
                .uri("/v1/posts/1");

        Result result = route(app, request);
        org.hamcrest.MatcherAssert.assertThat(result.status(), equalTo(GATEWAY_TIMEOUT));
    }

    @Test
    public void testCircuitBreakerOnShow() {
        PostRepository repository = app.injector().instanceOf(PostRepository.class);
        repository.create(new PostData("title-testCircuitBreakerOnShow", "body-testCircuitBreakerOnShow"));

        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/v1/posts/1");

        Result result = route(app, request);
        org.hamcrest.MatcherAssert.assertThat(result.status(), equalTo(SERVICE_UNAVAILABLE));
    }


}
