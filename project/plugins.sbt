// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.12")

// sbt-paradox, used for documentation
addSbtPlugin("com.lightbend.paradox" % "sbt-paradox" % "0.3.1")

// Load testing tool:
// http://gatling.io/docs/2.2.2/extensions/sbt_plugin.html
addSbtPlugin("io.gatling" % "gatling-sbt" % "2.2.2")

// Scala formatting: "sbt scalafmt"
addSbtPlugin("com.lucidchart" % "sbt-scalafmt" % "1.12")
