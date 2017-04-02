import play.api.test._
import play.api.test.Helpers._
import org.fluentlenium.core.filter.FilterConstructor._
import org.scalatestplus.play.PlaySpec

import org.scalatestplus.play._

class BrowserSpec extends PlaySpec {
  
  "Application" should {
    
    "work from within a browser" in {
      running(TestServer(3333), HTMLUNIT) { browser =>
        browser.goTo("http://localhost:3333/")
        
        browser.$("header h1").first.text() must equal("Play sample application — Computer database")
        browser.$("section h1").first.text() must equal("574 computers found")
        
        browser.$("#pagination li.current").first.text() must equal("Displaying 1 to 10 of 574")
        
        browser.$("#pagination li.next a").click()
        
        browser.$("#pagination li.current").first.text() must equal("Displaying 11 to 20 of 574")
        browser.$("#searchbox").fill().`with`("Apple")
        browser.$("#searchsubmit").click()
        
        browser.$("section h1").first.text() must equal("13 computers found")
        browser.$("a", withText("Apple II")).click()
        
        browser.$("section h1").first.text() must equal("Edit computer")

        browser.$("#discontinued").fill().`with`("")
        browser.$("input.primary").click()

        browser.$("section h1").first.text() must equal("574 computers found")
        browser.$(".alert-message").first.text() must equal("Done! Computer Apple II has been updated")
        
        browser.$("#searchbox").fill().`with`("Apple")
        browser.$("#searchsubmit").click()
        
        browser.$("a", withText("Apple II")).click()
        browser.$("input.danger").click()

        browser.$("section h1").first.text() must equal("573 computers found")
        browser.$(".alert-message").first.text() must equal("Done! Computer has been deleted")
        
        browser.$("#searchbox").fill().`with`("Apple")
        browser.$("#searchsubmit").click()
        
        browser.$("section h1").first.text() must equal("12 computers found")

      }
    }
    
  }
  
}
