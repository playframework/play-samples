import controllers.PersonController;
import models.Person;
import models.PersonRepository;
import org.junit.Test;
import play.api.mvc.Request;
import play.core.j.JavaContextComponents;
import play.core.j.JavaHelpers$;
import play.data.FormFactory;
import play.data.format.Formatters;
import play.i18n.MessagesApi;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.twirl.api.Content;

import javax.validation.Validator;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ForkJoinPool;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static play.mvc.Http.Status.OK;
import static play.mvc.Http.Status.SEE_OTHER;
import static play.test.Helpers.contentAsString;

/**
 * Simple (JUnit) tests that can call all parts of a play app.
 * <p>
 * https://www.playframework.com/documentation/latest/JavaTest
 */
public class UnitTest {

    @Test
    public void checkIndex() {
        // XXX This is a gap in the test API -- it should be play.test.Helpers.httpContext() by 2.6.0-M4
        // and JavaHelpers should be removed.
        Http.RequestBuilder request = Helpers.fakeRequest("GET", "/");

        // XXX This should be play.test.CSRFTokenHelper
        Http.RequestBuilder tokenRequest = play.api.test.CSRFTokenHelper.addCSRFToken(request);
        JavaContextComponents contextComponents = createContextComponents();
        Http.Context.current.set(createJavaContext(tokenRequest.build()._underlyingRequest(), contextComponents));

        PersonRepository repository = mock(PersonRepository.class);
        FormFactory formFactory = mock(FormFactory.class);
        HttpExecutionContext ec = new HttpExecutionContext(ForkJoinPool.commonPool());
        final PersonController controller = new PersonController(formFactory, repository, ec);
        final Result result = controller.index();

        assertThat(result.status()).isEqualTo(OK);
    }

    private Http.Context createJavaContext(Request<Http.RequestBody> request, JavaContextComponents contextComponents) {
        return JavaHelpers$.MODULE$.createJavaContext(request, contextComponents);
    }

    private JavaContextComponents createContextComponents() {
        return JavaHelpers$.MODULE$.createContextComponents();
    }

    @Test
    public void checkTemplate() {
        Content html = views.html.index.render();
        assertThat(html.contentType()).isEqualTo("text/html");
        assertThat(contentAsString(html)).contains("Add Person");
    }

    @Test
    public void checkAddPerson() {
        // Easier to mock out the form factory inputs here
        MessagesApi messagesApi = mock(MessagesApi.class);
        Validator validator = mock(Validator.class);
        FormFactory formFactory = new FormFactory(messagesApi, new Formatters(messagesApi), validator);

        // Don't need to be this involved in setting up the mock, but for demo it works:
        PersonRepository repository = mock(PersonRepository.class);
        Person person = new Person();
        person.id = 1L;
        person.name = "Steve";
        when(repository.add(any())).thenReturn(supplyAsync(() -> person));

        // Set up the request builder to reflect input
        final Http.RequestBuilder requestBuilder = new Http.RequestBuilder().method("post").bodyJson(Json.toJson(person));

        // Add in an Http.Context here using invokeWithContext:
        // XXX extending JavaHelpers is a cheat to get at JavaContextComponents easily, put this into helpers
        JavaContextComponents components = createContextComponents();
        final CompletionStage<Result> stage = Helpers.invokeWithContext(requestBuilder, components, () -> {
            HttpExecutionContext ec = new HttpExecutionContext(ForkJoinPool.commonPool());

            // Create controller and call method under test:
            final PersonController controller = new PersonController(formFactory, repository, ec);
            return controller.addPerson();
        });

        // Test the completed result
        await().atMost(1, SECONDS).until(() ->
            assertThat(stage.toCompletableFuture()).isCompletedWithValueMatching(result ->
                result.status() == SEE_OTHER, "Should redirect after operation"
            )
        );
    }

}
