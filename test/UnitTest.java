import controllers.PersonController;
import org.junit.Test;
import play.api.test.CSRFTokenHelper;
import play.core.j.JavaContextComponents;
import play.core.j.JavaHelpers;
import play.data.FormFactory;
import play.db.jpa.JPAApi;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.twirl.api.Content;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

/**
 * Simple (JUnit) tests that can call all parts of a play app.
 *
 * https://www.playframework.com/documentation/latest/JavaTest
 */
public class UnitTest implements JavaHelpers {

    @Test
    public void checkIndex() {
        // XXX This is a gap in the test API -- it should be play.test.Helpers.httpContext() by 2.6.0-M4
        // and JavaHelpers should be removed.
        Http.RequestBuilder request = Helpers.fakeRequest("GET", "/");

        // XXX This should be play.test.CSRFTokenHelper
        Http.RequestBuilder tokenRequest = play.api.test.CSRFTokenHelper.addCSRFToken(request);
        JavaContextComponents contextComponents = createContextComponents();
        Http.Context.current.set(createJavaContext(tokenRequest.build()._underlyingRequest(), contextComponents));

        JPAApi jpaApi = mock(JPAApi.class);
        FormFactory formFactory = mock(FormFactory.class);
        final PersonController controller = new PersonController(formFactory, jpaApi);
        final Result result = controller.index();

        assertEquals(OK, result.status());
    }

    @Test
    public void checkTemplate() {
        Content html = views.html.index.render();
        assertEquals("text/html", html.contentType());
        assertTrue(contentAsString(html).contains("Add Person"));
    }
}
