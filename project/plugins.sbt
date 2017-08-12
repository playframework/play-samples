// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.3")

// sbt-paradox, used for documentation
addSbtPlugin("com.lightbend.paradox" % "sbt-paradox" % "0.2.9")

// Load testing tool:
// http://gatling.io/docs/2.2.2/extensions/sbt_plugin.html
addSbtPlugin("io.gatling" % "gatling-sbt" % "2.2.1")

// Scala formatting: "sbt scalafmt"
// https://olafurpg.github.io/scalafmt
addSbtPlugin("com.geirsson" % "sbt-scalafmt" % "0.3.1")
