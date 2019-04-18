import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import controllers.PersonController;
import models.Person;
import models.PersonRepository;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.junit.Test;
import play.api.test.CSRFTokenHelper;
import play.data.FormFactory;
import play.data.format.Formatters;
import play.filters.csrf.CSRF;
import play.i18n.Messages;
import play.i18n.MessagesApi;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.twirl.api.Content;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.xml.validation.Validator;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ForkJoinPool;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
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
        Http.RequestBuilder request = CSRFTokenHelper.addCSRFToken(Helpers.fakeRequest("GET", "/"));

        PersonRepository repository = mock(PersonRepository.class);
        FormFactory formFactory = mock(FormFactory.class);
        HttpExecutionContext ec = new HttpExecutionContext(ForkJoinPool.commonPool());
        final PersonController controller = new PersonController(formFactory, repository, ec);
        final Result result = controller.index(request.build());

        assertThat(result.status()).isEqualTo(OK);
    }

    @Test
    public void checkTemplate() {
        Http.RequestBuilder request = CSRFTokenHelper.addCSRFToken(Helpers.fakeRequest("GET", "/"));
        Content html = views.html.index.render(request.build());
        assertThat(html.contentType()).isEqualTo("text/html");
        assertThat(contentAsString(html)).contains("Add Person");
    }

    @Test
    public void checkAddPerson() {
        // Don't need to be this involved in setting up the mock, but for demo it works:
        PersonRepository repository = mock(PersonRepository.class);
        Person person = new Person();
        person.id = 1L;
        person.name = "Steve";
        when(repository.add(any())).thenReturn(supplyAsync(() -> person));

        // Set up the request builder to reflect input
        Http.Request request = Helpers.fakeRequest("POST", "/").bodyJson(Json.toJson(person)).build().withTransientLang("es");

        // Easier to mock out the form factory inputs here
        Messages messages = mock(Messages.class);
        MessagesApi messagesApi = mock(MessagesApi.class);
        when(messagesApi.preferred(request)).thenReturn(messages);

        ValidatorFactory validatorFactory = Validation.byDefaultProvider().configure()
                .messageInterpolator(new ParameterMessageInterpolator())
                .buildValidatorFactory();

        Config config = ConfigFactory.load();
        FormFactory formFactory = new FormFactory(messagesApi, new Formatters(messagesApi), validatorFactory, config);

        // It is okay to use commonPool here since this is just a test.
        HttpExecutionContext ec = new HttpExecutionContext(ForkJoinPool.commonPool());

        // Create controller and call method under test:
        final PersonController controller = new PersonController(formFactory, repository, ec);

        CompletionStage<Result> stage = controller.addPerson(request);

        await().atMost(1, SECONDS).untilAsserted(
                () -> assertThat(stage.toCompletableFuture()).isCompletedWithValueMatching(
                        result -> result.status() == SEE_OTHER, "Should redirect after operation"
                )
        );
    }

}
