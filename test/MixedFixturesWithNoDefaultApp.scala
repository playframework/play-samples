/*
 * Copyright 2001-2014 Artima, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.scalatestplus.play._

import play.api.Application
import play.api.test._
import org.scalatest._

import org.scalatest.fixture._
import org.scalatest.selenium.WebBrowser
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxProfile
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.safari.SafariDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.ie.InternetExplorerDriver
import org.scalatestplus.play.BrowserFactory.UnavailableDriver
import org.openqa.selenium.safari.SafariDriver

trait MixedFixturesWithNoDefaultApp extends SuiteMixin with UnitFixture { this: fixture.Suite =>

  /**
   * `NoArg` subclass that provides an `Application` fixture.
   */
  abstract class App(appFun: => Application) extends NoArg {
    /**
     * Makes the passed-in `Application` implicit.
     */
    implicit def implicitApp: Application = app

    /**
     * The lazy instance created from passed <code>appFun</code>
     */
    lazy val app = appFun

    /**
     * Runs the passed in `Application` before executing the test body, ensuring it is closed after the test body completes.
     */
    override def apply() {
      def callSuper = super.apply()  // this is needed for Scala 2.10 to work
      Helpers.running(app)(callSuper)
    }
  }

  /**
   * `NoArg` subclass that provides a fixture composed of a `Application` and running `TestServer`.
   */
  abstract class Server(appFun: => Application, val port: Int = Helpers.testServerPort) extends NoArg {
    /**
     * Makes the passed in `Application` implicit.
     */
    implicit def implicitApp: Application = app

    /**
     * The lazy instance created from passed <code>appFun</code>
     */
    lazy val app = appFun

    /**
     * Implicit `PortNumber` instance that wraps `port`. The value returned from `portNumber.value`
     * will be same as the value of `port`.
     */
    implicit lazy val portNumber: PortNumber = PortNumber(port)

    /**
     * Runs a `TestServer` using the passed-in `Application` and  port before executing the
     * test body, ensuring both are stopped after the test body completes.
     */
    override def apply() {
      def callSuper = super.apply()  // this is needed for Scala 2.10 to work
      Helpers.running(TestServer(port, app))(callSuper)
    }
  }

  /**
   * `NoArg` subclass that provides a fixture composed of an `Application`, running `TestServer`, and
   * Selenium `HtmlUnitDriver`.
   */
  abstract class HtmlUnit(appFun: => Application, val port: Int = Helpers.testServerPort) extends WebBrowser with fixture.NoArg with HtmlUnitFactory {
    /**
     * A lazy implicit instance of `HtmlUnitDriver`. It will hold `UnavailableDriver` if `HtmlUnitDriver`
     * is not available in the running machine.
     */
    implicit lazy val webDriver: WebDriver = createWebDriver()

    /**
     * Makes the passed in `Application` implicit.
     */
    implicit def implicitApp: Application = app

    /**
     * The lazy instance created from passed <code>appFun</code>
     */
    lazy val app = appFun

    /**
     * Implicit `PortNumber` instance that wraps `port`. The value returned from `portNumber.value`
     * will be same as the value of `port`.
     */
    implicit lazy val portNumber: PortNumber = PortNumber(port)

    /**
     * Runs a `TestServer` using the passed-in `Application` and port before executing the
     * test body, which can use the `HtmlUnitDriver` provided by `webDriver`, ensuring all
     * are are stopped after the test body completes.
     */
    override def apply() {
      webDriver match {
        case UnavailableDriver(ex, errorMessage) =>
          ex match {
            case Some(e) => cancel(errorMessage, e)
            case None => cancel(errorMessage)
          }
        case _ =>
          def callSuper = super.apply()  // this is needed for Scala 2.10 to work
          try Helpers.running(TestServer(port, app))(callSuper)
          finally webDriver.close()
      }
    }
  }

  /**
   * `NoArg` subclass that provides a fixture composed of a `Application`, running `TestServer`, and
   * Selenium `FirefoxDriver`.
   */
  abstract class Firefox(appFun: => Application, val port: Int = Helpers.testServerPort) extends WebBrowser with NoArg with FirefoxFactory {

    /**
     * A lazy implicit instance of `FirefoxDriver`, it will hold `UnavailableDriver` if `FirefoxDriver`
     * is not available in the running machine.
     */
    implicit lazy val webDriver: WebDriver = createWebDriver()

    /**
     * Makes the passed in `Application` implicit.
     */
    implicit def implicitApp: Application = app

    /**
     * The lazy instance created from passed <code>appFun</code>
     */
    lazy val app = appFun

    /**
     * Implicit `PortNumber` instance that wraps `port`. The value returned from `portNumber.value`
     * will be same as the value of `port`.
     */
    implicit lazy val portNumber: PortNumber = PortNumber(port)

    /**
     * Runs a `TestServer` using the passed-in `Application` and port before executing the
     * test body, which can use the `FirefoxDriver` provided by `webDriver`, ensuring all
     * are are stopped after the test body completes.
     */
    override def apply() {
      webDriver match {
        case UnavailableDriver(ex, errorMessage) =>
          ex match {
            case Some(e) => cancel(errorMessage, e)
            case None => cancel(errorMessage)
          }
        case _ =>
          def callSuper = super.apply()  // this is needed for Scala 2.10 to work
          try Helpers.running(TestServer(port, app))(callSuper)
          finally webDriver.close()
      }
    }
  }

  /**
   * `NoArg` subclass that provides a fixture composed of an `Application`, running `TestServer`, and
   * Selenium `SafariDriver`.
   */
  abstract class Safari(appFun: => Application, val port: Int = Helpers.testServerPort) extends WebBrowser with NoArg with SafariFactory {
    /**
     * A lazy implicit instance of `SafariDriver`, it will hold `UnavailableDriver` if `SafariDriver`
     * is not available in the running machine.
     */
    implicit lazy val webDriver: WebDriver = createWebDriver()

    /**
     * Makes the passed in `Application` implicit.
     */
    implicit def implicitApp: Application = app

    /**
     * The lazy instance created from passed <code>appFun</code>
     */
    lazy val app = appFun

    /**
     * Implicit `PortNumber` instance that wraps `port`. The value returned from `portNumber.value`
     * will be same as the value of `port`.
     */
    implicit lazy val portNumber: PortNumber = PortNumber(port)

    /**
     * Runs a `TestServer` using the passed-in `Application` and port before executing the
     * test body, which can use the `SafariDriver` provided by `webDriver`, ensuring all
     * are are stopped after the test body completes.
     */
    override def apply() {
      webDriver match {
        case UnavailableDriver(ex, errorMessage) =>
          ex match {
            case Some(e) => cancel(errorMessage, e)
            case None => cancel(errorMessage)
          }
        case _ =>
          def callSuper = super.apply()  // this is needed for Scala 2.10 to work
          try Helpers.running(TestServer(port, app))(callSuper)
          finally webDriver.quit()
      }
    }
  }

  /**
   * `NoArg` subclass that provides a fixture composed of an `Application`, running `TestServer`, and
   * Selenium `ChromeDriver`.
   */
  abstract class Chrome(appFun: => Application, val port: Int = Helpers.testServerPort) extends WebBrowser with NoArg with ChromeFactory {
    /**
     * A lazy implicit instance of `ChromeDriver`, it will hold `UnavailableDriver` if `ChromeDriver`
     * is not available in the running machine.
     */
    implicit lazy val webDriver: WebDriver = createWebDriver()

    /**
     * Makes the passed in `Application` implicit.
     */
    implicit def implicitApp: Application = app

    /**
     * The lazy instance created from passed <code>appFun</code>
     */
    lazy val app = appFun

    /**
     * Implicit `PortNumber` instance that wraps `port`. The value returned from `portNumber.value`
     * will be same as the value of `port`.
     */
    implicit lazy val portNumber: PortNumber = PortNumber(port)

    /**
     * Runs a `TestServer` using the passed-in `Application` and port before executing the
     * test body, which can use the `ChromeDriver` provided by `webDriver`, ensuring all
     * are are stopped after the test body completes.
     */
    override def apply() {
      webDriver match {
        case UnavailableDriver(ex, errorMessage) =>
          ex match {
            case Some(e) => cancel(errorMessage, e)
            case None => cancel(errorMessage)
          }
        case _ =>
          def callSuper = super.apply()  // this is needed for Scala 2.10 to work
          try Helpers.running(TestServer(port, app))(callSuper)
          finally webDriver.quit()
      }
    }
  }

  /**
   * `NoArg` subclass that provides a fixture composed of an `Application`, running `TestServer`, and
   * Selenium `InternetExplorerDriver`.
   */
  abstract class InternetExplorer(appFun: => Application, val port: Int = Helpers.testServerPort) extends WebBrowser with NoArg with InternetExplorerFactory {
    /**
     * A lazy implicit instance of `InternetExplorerDriver`, it will hold `UnavailableDriver` if `InternetExplorerDriver`
     * is not available in the running machine.
     */
    implicit lazy val webDriver: WebDriver = createWebDriver()

    /**
     * Makes the passed in `Application` implicit.
     */
    implicit def implicitApp: Application = app

    /**
     * The lazy instance created from passed <code>appFun</code>
     */
    lazy val app = appFun

    /**
     * Implicit `PortNumber` instance that wraps `port`. The value returned from `portNumber.value`
     * will be same as the value of `port`.
     */
    implicit lazy val portNumber: PortNumber = PortNumber(port)

    /**
     * Runs a `TestServer` using the passed-in `Application` and port before executing the
     * test body, which can use the `InternetExplorerDriver` provided by `webDriver`, ensuring all
     * are are stopped after the test body completes.
     */
    override def apply() {
      webDriver match {
        case UnavailableDriver(ex, errorMessage) =>
          ex match {
            case Some(e) => cancel(errorMessage, e)
            case None => cancel(errorMessage)
          }
        case _ =>
          def callSuper = super.apply()  // this is needed for Scala 2.10 to work
          try Helpers.running(TestServer(port, app))(callSuper)
          finally webDriver.close()
      }
    }
  }
}

