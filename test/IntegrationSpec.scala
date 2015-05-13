import org.specs2.mutable._

class IntegrationSpec extends Specification {

  "Application" should {

    "work from within a browser" in new WithGreetingApplicationBrowser {

      browser.goTo("http://localhost:" + port)

      browser.pageSource must contain("Your new application is ready.")
    }
  }
}
