name := """play-2.6-log4j2"""

version := "1.0-SNAPSHOT"

val log4jVersion = "2.10.0"

// Run with activator -Dlog4j.configurationFile=conf/log4j2.xml
lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .disablePlugins(PlayLogback).settings(
  libraryDependencies ++= Seq(
    guice,
    "org.apache.logging.log4j" % "log4j-slf4j-impl" % log4jVersion,
    "org.apache.logging.log4j" % "log4j-api" % log4jVersion,
    "org.apache.logging.log4j" % "log4j-core" % log4jVersion
  )
)

scalaVersion in ThisBuild := "2.12.4"

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
