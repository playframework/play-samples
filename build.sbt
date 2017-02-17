name := """macwire-di"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.1"

libraryDependencies += guice
libraryDependencies += ws % Test // only used in tests right now
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0-M2" % Test
libraryDependencies += "com.softwaremill.macwire" %% "macros" % "2.2.2" % "provided"
libraryDependencies += "com.softwaremill.macwire" %% "util" % "2.2.2"
libraryDependencies += "com.softwaremill.macwire" %% "proxy" % "2.2.2"
