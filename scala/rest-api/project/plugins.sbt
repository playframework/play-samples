// The Play plugin
resolvers += Resolver.sonatypeCentralSnapshots
addSbtPlugin("org.playframework" % "sbt-plugin" % "3.1.0-M10-7902ed14-SNAPSHOT")

// sbt-paradox, used for documentation
addSbtPlugin("com.lightbend.paradox" % "sbt-paradox" % "0.11.1")

// Load testing tool:
// https://gatling.io/docs/gatling/reference/current/extensions/sbt_plugin/
addSbtPlugin("io.gatling" % "gatling-sbt" % "4.20.2")
