import static io.fluentlenium.core.filter.FilterConstructor.withText;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import play.api.test.Helpers;
import play.test.WithBrowser;

public class BrowserTest extends WithBrowser {

  @Test
  public void testBrowser() {
    browser.goTo("http://localhost:" + port);

    assertThat(browser.$(".navbar-brand").first().text(), equalTo("Play sample application — Computer database"));
    assertThat(browser.$("#page-title").first().text(), equalTo("574 computers found"));

    assertThat(browser.$(".pagination li[aria-current]").first().text(), equalTo("Displaying 1 to 10 of 574"));

    browser.$(".pagination li.next a").click();

    assertThat(browser.$(".pagination li[aria-current]").first().text(), equalTo("Displaying 11 to 20 of 574"));

    browser.$("#searchbox").fill().with("Apple");
    browser.$("#searchsubmit").click();

    assertThat(browser.$("#page-title").first().text(), equalTo("13 computers found"));
    browser.$("a", withText("Apple II")).click();

    assertThat(browser.$("#page-title").first().text(), equalTo("Edit computer"));

    browser.$("#name").fill().with("");
    browser.$("button.btn-success").click();

    assertThat(browser.$("#name").attributes("class").get(0), equalTo("form-control is-invalid"));
    assertThat(browser.$("div#input-for-name span").first().text(), equalTo("This field is required"));

    browser.$("#name").fill().with("Apple IIa");

    browser.$("button.btn-success").click();

    assertThat(browser.$("#page-title").first().text(), equalTo("574 computers found"));
    assertThat(browser.$(".alert-warning").first().text(), equalTo("Done! Computer Apple IIa has been updated"));

    browser.$("#searchbox").fill().with("Apple");
    browser.$("#searchsubmit").click();

    browser.$("a", withText("Apple IIa")).click();
    browser.$("button.btn-danger").click();

    browser.takeHtmlDump("target/delete.html");

    assertThat(browser.$("#page-title").first().text(), equalTo("573 computers found"));
    assertThat(browser.$(".alert-warning").first().text(), equalTo("Done! Computer has been deleted"));

    browser.$("#searchbox").fill().with("Apple");
    browser.$("#searchsubmit").click();

    assertThat(browser.$("#page-title").first().text(), equalTo("12 computers found"));
  }

}
