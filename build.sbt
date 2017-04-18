name := """macwire-di"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.2"

libraryDependencies += guice
libraryDependencies += ws % Test // only used in tests right now
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0-M3" % Test
libraryDependencies += "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided"
libraryDependencies += "com.softwaremill.macwire" %% "util" % "2.3.0"
libraryDependencies += "com.softwaremill.macwire" %% "proxy" % "2.3.0"
