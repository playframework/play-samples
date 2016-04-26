import org.scalatest.concurrent.{Eventually, IntegrationPatience}
import org.scalatest.{MustMatchers, OptionValues, fixture}
import org.scalatestplus.play.{PortNumber, WsScalaTestClient}
import play.api.libs.ws.{WSClient, WSRequest}
import play.api.mvc.Call

abstract class MixedPlaySpecWithNoDefaultApp extends fixture.WordSpec
  with MustMatchers
  with OptionValues
  with MixedFixturesWithNoDefaultApp
  with Eventually
  with IntegrationPatience
  with WsScalaTestClient
{

  //def wsCall(call: Call)(implicit portNumber: PortNumber, wsClient: WSClient): WSRequest = doCall(call.url, wsClient, portNumber)

  // def wsUrl(url: String)(implicit portNumber: PortNumber, wsClient: WSClient): WSRequest = doCall(url, wsClient, portNumber)

  //private def doCall(url: String, wsClient: WSClient, portNumber: PortNumber) = {
  //  wsClient.url("http://localhost:" + portNumber.value + url)
  //}
}
