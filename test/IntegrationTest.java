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

            assertThat(browser.$("header h1").first().getText(), equalTo("Play sample application — Computer database"));
            assertThat(browser.$("section h1").first().getText(), equalTo("574 computers found"));

            assertThat(browser.$("#pagination li.current").first().getText(), equalTo("Displaying 1 to 10 of 574"));

            browser.$("#pagination li.next a").click();

            assertThat(browser.$("#pagination li.current").first().getText(), equalTo("Displaying 11 to 20 of 574"));
            browser.$("#searchbox").text("Apple");
            browser.$("#searchsubmit").click();

            assertThat(browser.$("section h1").first().getText(), equalTo("13 computers found"));
            browser.$("a", withText("Apple II")).click();

            assertThat(browser.$("section h1").first().getText(), equalTo("Edit computer"));

            browser.$("#discontinued").text("10-10-2001");
            browser.$("input.primary").click();

            assertThat(browser.$("section h1").first().getText(), equalTo("574 computers found"));
            assertThat(browser.$(".alert-message").first().getText(), equalTo("Done! Computer Apple II has been updated"));

            browser.$("#searchbox").text("Apple");
            browser.$("#searchsubmit").click();

            browser.$("a", withText("Apple II")).click();
            browser.$("input.danger").click();

            assertThat(browser.$("section h1").first().getText(), equalTo("573 computers found"));
            assertThat(browser.$(".alert-message").first().getText(), equalTo("Done! Computer has been deleted"));

            browser.$("#searchbox").text("Apple");
            browser.$("#searchsubmit").click();

            assertThat(browser.$("section h1").first().getText(), equalTo("12 computers found"));
        });
    }

}
