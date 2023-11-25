// The Play plugin
addSbtPlugin("org.playframework" % "sbt-plugin" % "3.0.0")

// sbt-paradox, used for documentation
addSbtPlugin("com.lightbend.paradox" % "sbt-paradox" % "0.10.5")

// Load testing tool:
// http://gatling.io/docs/2.2.2/extensions/sbt_plugin.html
addSbtPlugin("io.gatling" % "gatling-sbt" % "4.6.0")

// Scala formatting: "sbt scalafmt"
addSbtPlugin("com.lucidchart" % "sbt-scalafmt" % "1.16")
