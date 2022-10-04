lazy val plugins = (project in file(".")).settings(
  scalaVersion := "2.12.17", // TODO: remove when upgraded to sbt 1.8.0 (maybe even 1.7.2), see https://github.com/sbt/sbt/pull/7021
)

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies += "com.h2database" % "h2" % "1.4.196"

// Database migration
// https://github.com/flyway/flyway-sbt
addSbtPlugin("io.github.davidmweber" % "flyway-sbt" % "6.2.2")

// Slick code generation
// https://github.com/tototoshi/sbt-slick-codegen
addSbtPlugin("com.github.tototoshi" % "sbt-slick-codegen" % "1.4.0")

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.9.0-M2")
