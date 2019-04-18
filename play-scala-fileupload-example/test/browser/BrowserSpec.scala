package browser

import java.nio.file.{Path, Files => JFiles}

import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import org.scalatestplus.play.{HtmlUnitFactory, OneBrowserPerSuite, PlaySpec}

class BrowserSpec extends PlaySpec with GuiceOneServerPerSuite with OneBrowserPerSuite with HtmlUnitFactory {

  "Browser" must {
    "upload file" in {
      val tmpPath = JFiles.createTempFile(null, null)
      writeFile(tmpPath, "hello")

      // http://doc.scalatest.org/3.0.0/index.html#org.scalatest.selenium.WebBrowser
      go to s"http://localhost:$port/"
      click on name("name")
      pressKeys(tmpPath.toAbsolutePath.toString)
      submit()

      eventually { pageSource mustBe "file size = 5" }
    }
  }

  def writeFile(path: Path, content: String): Path = {
    JFiles.write(path, content.getBytes)
  }

}
