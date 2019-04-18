package controllers

import java.io.IOException

import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.play._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test._
import play.shaded.ahc.org.asynchttpclient.AsyncHttpClient
import play.shaded.ahc.org.asynchttpclient.ws.WebSocket

import scala.compat.java8.FutureConverters
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

class HomeControllerSpec extends PlaySpec with ScalaFutures with IntegrationPatience {

  "HomeController" should {

    "reject a websocket flow if the origin is set incorrectly" in WsTestClient.withClient { client =>

      // Pick a non standard port that will fail the (somewhat contrived) origin check...
      lazy val port: Int = 31337
      val app = new GuiceApplicationBuilder().build()
      Helpers.running(TestServer(port, app)) {
        val myPublicAddress = s"localhost:$port"
        val serverURL = s"ws://$myPublicAddress/chat"

        val asyncHttpClient: AsyncHttpClient = client.underlying[AsyncHttpClient]

        val webSocketClient = new WebSocketClient(asyncHttpClient)
        try {
          val origin = "ws://example.com/ws/chat"
          val listener = new WebSocketClient.LoggingListener
          val completionStage = webSocketClient.call(serverURL, origin, listener)
          val f = FutureConverters.toScala(completionStage)
          Await.result(f, atMost = 1000 millis)
          listener.getThrowable mustBe a[IOException]
        } catch {
          case e: IllegalStateException =>
            e mustBe an [IllegalStateException]

          case e: java.util.concurrent.ExecutionException =>
            val foo = e.getCause
            foo mustBe an [IOException]
        }
      }
    }

    "accept a websocket flow if the origin is set correctly" in WsTestClient.withClient { client =>
      lazy val port: Int = Helpers.testServerPort
      val app = new GuiceApplicationBuilder().build()
      Helpers.running(TestServer(port, app)) {
        val myPublicAddress = s"localhost:$port"
        val serverURL = s"ws://$myPublicAddress/chat"

        val asyncHttpClient: AsyncHttpClient = client.underlying[AsyncHttpClient]

        val webSocketClient = new WebSocketClient(asyncHttpClient)

        val origin = serverURL
        val listener = new WebSocketClient.LoggingListener
        val completionStage = webSocketClient.call(serverURL, origin, listener)
        val f = FutureConverters.toScala(completionStage)

        whenReady(f, timeout = Timeout(1 second)) { webSocket =>
          webSocket mustBe a [WebSocket]
        }
      }
    }
  }

}
