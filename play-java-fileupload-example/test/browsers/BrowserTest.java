package browsers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.fluentlenium.core.domain.FluentWebElement;
import org.junit.Test;
import org.openqa.selenium.By;
import play.test.WithBrowser;

import static org.junit.Assert.assertTrue;

public class BrowserTest extends WithBrowser {

    @Test
    public void uploadFile() throws IOException {
        Path tmpPath = Files.createTempFile(null, null);
        Files.write(tmpPath, "hello".getBytes());

        // http://fluentlenium.org/docs/#filling-forms
        // https://saucelabs.com/resources/articles/best-practices-tips-selenium-file-upload
        browser.goTo("/");
        FluentWebElement nameElement = browser.find(By.name("name")).first();
        nameElement.click();
        nameElement.fill().with(tmpPath.toAbsolutePath().toString());
        nameElement.submit();

        assertTrue(browser.pageSource().equals("file size = 5"));
    }

}
