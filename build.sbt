name := """macwire-di"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies += guice
libraryDependencies += ws % Test // only used in tests right now
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
libraryDependencies += "com.softwaremill.macwire" %% "macros" % "2.2.2" % "provided"
libraryDependencies += "com.softwaremill.macwire" %% "util" % "2.2.2" 
libraryDependencies += "com.softwaremill.macwire" %% "proxy" % "2.2.2"
