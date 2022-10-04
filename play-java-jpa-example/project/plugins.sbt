lazy val plugins = (project in file(".")).settings(
  scalaVersion := "2.12.17", // TODO: remove when upgraded to sbt 1.8.0 (maybe even 1.7.2), see https://github.com/sbt/sbt/pull/7021
)

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.9.0-M2")

// Web plugins
addSbtPlugin("com.typesafe.sbt" % "sbt-coffeescript" % "1.0.2")
addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.1.2")
addSbtPlugin("com.typesafe.sbt" % "sbt-jshint" % "1.0.6")
addSbtPlugin("com.typesafe.sbt" % "sbt-rjs" % "1.0.10")
addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.4")
addSbtPlugin("com.typesafe.sbt" % "sbt-mocha" % "1.1.2")
addSbtPlugin("org.irundaia.sbt" % "sbt-sassify" % "1.4.12")
