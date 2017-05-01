name := """play-scala-macwire-di-example"""

version := "2.6.x"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.2"

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0-M3" % Test
libraryDependencies += "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided"
