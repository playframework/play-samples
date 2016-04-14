import com.google.inject.*;

import java.util.*;

import org.junit.runners.MethodSorters;
import play.*;
import play.inject.guice.*;
import play.mvc.*;

import play.test.*;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.junit.*;

import javax.inject.Inject;

// Use FixMethodOrder to run the tests sequentially
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FunctionalTest {

    @Inject
    Application application;

    @Before
    public void setup() {
        Module testModule = new AbstractModule() {
            @Override
            public void configure() {
                // Install custom test binding here
            }
        };

        GuiceApplicationBuilder builder = new GuiceApplicationLoader()
                .builder(new ApplicationLoader.Context(Environment.simple()))
                .overrides(testModule);
        Guice.createInjector(builder.applicationModule()).injectMembers(this);

        Helpers.start(application);
    }

    @After
    public void teardown() {
        Helpers.stop(application);
    }

    @Test
    public void redirectHomePage() {
        Result result = Helpers.route(application, controllers.routes.HomeController.index());

        assertThat(result.status(), equalTo(Helpers.SEE_OTHER));
        assertThat(result.redirectLocation().get(), equalTo("/computers"));
    }
    
    @Test
    public void listComputersOnTheFirstPage() {
        Result result =  Helpers.route(application, controllers.routes.HomeController.list(0, "name", "asc", ""));

        assertThat(result.status(), equalTo(Helpers.OK));
        assertThat(Helpers.contentAsString(result), containsString("574 computers found"));
    }
    
    @Test
    public void filterComputerByName() {
        Result result = Helpers.route(application, controllers.routes.HomeController.list(0, "name", "asc", "Apple"));

        assertThat(result.status(), equalTo(Helpers.OK));
        assertThat(Helpers.contentAsString(result), containsString("13 computers found"));
    }
    
    @Test
    public void createANewComputer() {
        Result result = Helpers.route(application, controllers.routes.HomeController.save());

        assertThat(result.status(), equalTo(Helpers.BAD_REQUEST));

        Map<String,String> data = new HashMap<>();
        data.put("name", "FooBar");
        data.put("introduced", "badbadbad");
        data.put("company.id", "1");

        String saveUrl = controllers.routes.HomeController.save().url();
        result = Helpers.route(application, Helpers.fakeRequest().bodyForm(data).method("POST").uri(saveUrl));

        assertThat(result.status(), equalTo(Helpers.BAD_REQUEST));
        assertThat(Helpers.contentAsString(result), containsString("<option value=\"1\" selected >Apple Inc.</option>"));
        //  <input type="date" id="introduced" name="introduced" value="badbadbad" aria-describedby="introduced_info_0 introduced_error_0" aria-invalid="true" class="form-control">
        assertThat(Helpers.contentAsString(result), containsString("<input type=\"date\" id=\"introduced\" name=\"introduced\" value=\"badbadbad\" "));
        // <input type="text" id="name" name="name" value="FooBar" aria-describedby="name_info_0" required="true" class="form-control">
        assertThat(Helpers.contentAsString(result), containsString("<input type=\"text\" id=\"name\" name=\"name\" value=\"FooBar\" "));

        data.put("introduced", "2011-12-24");

        result = Helpers.route(
            application,
            Helpers.fakeRequest().bodyForm(data).method("POST").uri(saveUrl)
        );

        assertThat(result.status(), equalTo(Helpers.SEE_OTHER));
        assertThat(result.redirectLocation().get(), equalTo("/computers"));
        assertThat(result.flash().get("success"), equalTo("Computer FooBar has been created"));

        result = Helpers.route(application, controllers.routes.HomeController.list(0, "name", "asc", "FooBar"));
        assertThat(result.status(), equalTo(Helpers.OK));
        assertThat(Helpers.contentAsString(result), containsString("One computer found"));
    }
    
}
