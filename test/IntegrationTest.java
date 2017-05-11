import dagger.MyApplicationLoader;
import org.junit.Test;
import play.Application;
import play.ApplicationLoader;
import play.Environment;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;

import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.test.Helpers.*;

public class IntegrationTest extends WithApplication {

    @Override
    protected Application provideApplication() {
        return new MyApplicationLoader().load(ApplicationLoader.Context.create(Environment.simple()));
    }

    @Test
    public void testIndex() {
        Http.RequestBuilder request = Helpers.fakeRequest();
        request.uri(controllers.routes.TimeController.index().url());
        // passing app in explicitly here is key since route() overloads without it use the deprecated
        // static Application references
        Result result = route(app, request);
        assertEquals(result.status(), OK);
        String content = contentAsString(result);

        List<String> timezones = Arrays.asList(TimeZone.getAvailableIDs());
        for (String timezone : timezones) {
            assertTrue(content.contains(timezone));
        }
    }

}
