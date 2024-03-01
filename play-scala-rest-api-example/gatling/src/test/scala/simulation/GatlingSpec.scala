package simulation

import io.gatling.core.Predef._
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration._
import scala.language.postfixOps

// run with "sbt gatling:test" on another machine so you don't have resources contending.
// https://gatling.io/docs/gatling/reference/current/core/simulation/
class GatlingSpec extends Simulation {

  // change this to another machine, make sure you have Play running in producion mode
  // i.e. sbt stage / sbt dist and running the script
  val httpConf: HttpProtocolBuilder = http.baseUrl("http://localhost:9000/v1/posts")

  val indexReq = repeat(500) {
    exec(
      http("Index").get("/").check(status.is(200))
    )
  }

  val readClientsScenario = scenario("Clients").exec(indexReq).pause(1)

  setUp(
    // For reference, this hits 25% CPU on a 5820K with 32 GB, running both server and load test.
    // In general, you want to ramp up load slowly, and measure with a JVM that has been "warmed up":
    // https://groups.google.com/forum/#!topic/gatling/mD15aj-fyo4
    readClientsScenario.inject(rampUsers(2000).during(100 seconds)).protocols(httpConf)
  )
}
