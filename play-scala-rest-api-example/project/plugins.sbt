// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.9.4")

// sbt-paradox, used for documentation
addSbtPlugin("com.lightbend.paradox" % "sbt-paradox" % "0.10.7")

// Load testing tool:
// https://gatling.io/docs/gatling/reference/current/extensions/sbt_plugin/
addSbtPlugin("io.gatling" % "gatling-sbt" % "4.8.2")

// Scala formatting: "sbt scalafmt"
addSbtPlugin("com.lucidchart" % "sbt-scalafmt" % "1.16")
