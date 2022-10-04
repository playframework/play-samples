lazy val plugins = (project in file(".")).settings(
  scalaVersion := "2.12.17", // TODO: remove when upgraded to sbt 1.8.0 (maybe even 1.7.2), see https://github.com/sbt/sbt/pull/7021
)

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.9.0-M2")
