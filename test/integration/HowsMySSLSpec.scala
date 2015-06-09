package integration

import com.typesafe.config.ConfigFactory
import org.junit.runner._
import org.specs2.runner._
import play.api.Mode
import play.api.libs.json.Json
import play.api.test._

import scala.concurrent.duration._

@RunWith(classOf[JUnitRunner])
class HowsMySSLSpec extends PlaySpecification with https.ClientMethods {

  "WS" should {

    "connect to a remote server " in {
      val input = """play.ws.ssl {
                    |  //enabledProtocols = [ TLSv1.2 ]
                    |}
                  """.stripMargin
      val config = play.api.Configuration(ConfigFactory.parseString(input).withFallback(ConfigFactory.defaultReference()))
      val environment = play.api.Environment.simple(new java.io.File("./conf"), Mode.Dev)
      val client = createClient(config, environment)

      val response = await(client.url("https://www.howsmyssl.com/a/check").get())(2 seconds)
      val jsonOutput = response.json
      val expected =
        """{"given_cipher_suites":[
          |   "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256",
          |   "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256",
          |   "TLS_RSA_WITH_AES_128_CBC_SHA256",
          |   "TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA256",
          |   "TLS_ECDH_RSA_WITH_AES_128_CBC_SHA256",
          |   "TLS_DHE_RSA_WITH_AES_128_CBC_SHA256",
          |   "TLS_DHE_DSS_WITH_AES_128_CBC_SHA256",
          |   "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA",
          |   "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA",
          |   "TLS_RSA_WITH_AES_128_CBC_SHA",
          |   "TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA",
          |   "TLS_ECDH_RSA_WITH_AES_128_CBC_SHA",
          |   "TLS_DHE_RSA_WITH_AES_128_CBC_SHA",
          |   "TLS_DHE_DSS_WITH_AES_128_CBC_SHA",
          |   "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256",
          |   "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256",
          |   "TLS_RSA_WITH_AES_128_GCM_SHA256",
          |   "TLS_ECDH_ECDSA_WITH_AES_128_GCM_SHA256",
          |   "TLS_ECDH_RSA_WITH_AES_128_GCM_SHA256",
          |   "TLS_DHE_RSA_WITH_AES_128_GCM_SHA256",
          |   "TLS_DHE_DSS_WITH_AES_128_GCM_SHA256",
          |   "TLS_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA",
          |   "TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA",
          |   "TLS_RSA_WITH_3DES_EDE_CBC_SHA",
          |   "TLS_ECDH_ECDSA_WITH_3DES_EDE_CBC_SHA",
          |   "TLS_ECDH_RSA_WITH_3DES_EDE_CBC_SHA",
          |   "TLS_DHE_RSA_WITH_3DES_EDE_CBC_SHA",
          |   "TLS_DHE_DSS_WITH_3DES_EDE_CBC_SHA",
          |   "TLS_EMPTY_RENEGOTIATION_INFO_SCSV"
          |   ],
          |  "ephemeral_keys_supported":true,
          |  "session_ticket_supported":false,
          |  "tls_compression_supported":false,
          |  "unknown_cipher_suite_supported":false,
          |  "beast_vuln":false,
          |  "able_to_detect_n_minus_one_splitting":false,
          |  "insecure_cipher_suites":{},
          |  "tls_version":"TLS 1.2",
          |  "rating":"Improvable"
          |}
        """.stripMargin
      val expectedJson = Json.parse(expected)

      jsonOutput must beEqualTo(expectedJson)
    }
  }

}