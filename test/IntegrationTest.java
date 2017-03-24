import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import static play.test.Helpers.*;

import static org.fluentlenium.core.filter.FilterConstructor.*;

public class IntegrationTest {

    @Test
    public void testIntegration() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), HTMLUNIT, browser -> {
            browser.goTo("http://localhost:3333");

            assertThat(browser.$("header h1").first().text(), equalTo("Play sample application — Computer database"));
            assertThat(browser.$("section h1").first().text(), equalTo("574 computers found"));

            assertThat(browser.$("#pagination li.current").first().text(), equalTo("Displaying 1 to 10 of 574"));

            browser.$("#pagination li.next a").click();

            assertThat(browser.$("#pagination li.current").first().text(), equalTo("Displaying 11 to 20 of 574"));
            browser.$("#searchbox").fill().with("Apple");
            browser.$("#searchsubmit").click();

            assertThat(browser.$("section h1").first().text(), equalTo("13 computers found"));
            browser.$("a", withText("Apple II")).click();

            assertThat(browser.$("section h1").first().text(), equalTo("Edit computer"));

            browser.$("#discontinued").fill().with("10-10-2001");
            browser.$("input.primary").click();

            assertThat(browser.$("dl.error").size(), equalTo(1));
            assertThat(browser.$("dl.error label").first().text() ,equalTo("Discontinued date"));

            browser.$("#discontinued").fill().with("xxx");
            browser.$("input.primary").click();

            assertThat(browser.$("dl.error").size(), equalTo(1));
            assertThat(browser.$("dl.error label").first().text(), equalTo("Discontinued date"));

            browser.$("#discontinued").fill().with("");
            browser.$("input.primary").click();

            assertThat(browser.$("section h1").first().text(), equalTo("574 computers found"));
            assertThat(browser.$(".alert-message").first().text(), equalTo("Done! Computer Apple II has been updated"));

            browser.$("#searchbox").fill().with("Apple");
            browser.$("#searchsubmit").click();

            browser.$("a", withText("Apple II")).click();
            browser.$("input.danger").click();

            assertThat(browser.$("section h1").first().text(), equalTo("573 computers found"));
            assertThat(browser.$(".alert-message").first().text(), equalTo("Done! Computer has been deleted"));

            browser.$("#searchbox").fill().with("Apple");
            browser.$("#searchsubmit").click();

            assertThat(browser.$("section h1").first().text(), equalTo("12 computers found"));
        });
    }

}
