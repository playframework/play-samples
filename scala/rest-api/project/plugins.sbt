// The Play plugin
resolvers += Resolver.sonatypeCentralSnapshots
addSbtPlugin("org.playframework" % "sbt-plugin" % "3.1.0-M3")

// sbt-paradox, used for documentation
addSbtPlugin("com.lightbend.paradox" % "sbt-paradox" % "0.10.7")

// Load testing tool:
// https://gatling.io/docs/gatling/reference/current/extensions/sbt_plugin/
addSbtPlugin("io.gatling" % "gatling-sbt" % "4.16.1")

// Scala formatting: "sbt scalafmt"
addSbtPlugin("com.lucidchart" % "sbt-scalafmt" % "1.16")
