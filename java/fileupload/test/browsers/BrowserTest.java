package browsers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.codeborne.selenide.SelenideElement;
import org.junit.Test;
import org.openqa.selenium.By;
import play.test.WithBrowser;

import static org.junit.Assert.assertTrue;

public class BrowserTest extends WithBrowser {

    @Test
    public void uploadFile() throws IOException {
        Path tmpPath = Files.createTempFile(null, null);
        Files.write(tmpPath, "hello".getBytes());

        // https://saucelabs.com/resources/articles/best-practices-tips-selenium-file-upload
        browser.goTo("/");
        SelenideElement nameElement = browser.find(By.name("name")).first();
        nameElement.uploadFile(tmpPath.toFile());
        nameElement.submit();

        assertTrue(browser.pageSource().equals("file size = 5"));
    }

}
