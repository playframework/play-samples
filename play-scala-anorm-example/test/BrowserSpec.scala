import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneServerPerTest

/**
 * Runs a browser based test against the application.
 *
 * http://doc.scalatest.org/3.0.0/index.html#org.scalatest.selenium.WebBrowser
 * http://www.scalatest.org/user_guide/using_selenium
 * https://www.playframework.com/documentation/latest/ScalaFunctionalTestingWithScalaTest#Testing-with-a-web-browser
 */
class BrowserSpec extends PlaySpec
  with OneBrowserPerTest
  with GuiceOneServerPerTest
  with HtmlUnitFactory {

  def $(str: String) = find(cssSelector(str)).getOrElse(throw new IllegalArgumentException(s"Cannot find $str"))

  "Application" should {
    
    "work from within a browser" in {
      System.setProperty("webdriver.gecko.driver", "/path/to/geckodriver")

      go to(s"http://localhost:$port/")

      find("header-title").get.text must equal("Play sample application — Computer database")
        find("section-title").get.text must equal("574 computers found")
        
      find(cssSelector(".current")).get.text must equal("Displaying 1 to 10 of 574")

        click on $("#pagination li.next a")
        
        $("#pagination li.current").text must equal("Displaying 11 to 20 of 574")

        click on id("searchbox")
        enter("Apple")
        click on id("searchsubmit")
        
        $("section h1").text must equal("13 computers found")
        click on linkText("Apple II")
        
        click on id("discontinued")
        enter("xxx")
        submit()

        find(cssSelector("dl.error")) must not be empty
        $("dl.error label").text must equal("Discontinued date")

        click on id("discontinued")
        enter("")
        submit()

        $("section h1").text must equal("574 computers found")
        $(".alert-message").text must equal("Done! Computer Apple II has been updated")

        click on id("searchbox")
        enter("Apple")
        submit
        
        click on linkText("Apple II")
        click on $("input.danger")

        $("section h1").text must equal("573 computers found")
        $(".alert-message").text must equal("Done! Computer has been deleted")
        
        click on $("#searchbox")
        enter("Apple")
        submit()  // $("#searchsubmit").click()
        
        $("section h1").text must equal("12 computers found")
    }
  }
}
