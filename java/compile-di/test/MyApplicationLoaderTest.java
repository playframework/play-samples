import org.junit.Assert;
import org.junit.Test;

import play.Application;
import play.Environment;
import play.ApplicationLoader.Context;
import play.mvc.Result;
import play.mvc.Http.RequestBuilder;
import play.test.Helpers;
import play.test.WithApplication;

public class MyApplicationLoaderTest extends WithApplication {

    @Override
    protected Application provideApplication() {
        Context context = new Context(Environment.simple());
        return new MyApplicationLoader().load(context);
    }

    @Test
    public void shouldLoadApplicationWithRouter() {
        RequestBuilder request = Helpers.fakeRequest();
        Result result = Helpers.route(app, request, 2000);
        Assert.assertEquals(200, result.status());
    }
}