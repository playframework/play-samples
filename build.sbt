name := """play-kalium"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.1"

libraryDependencies += guice
libraryDependencies += "org.abstractj.kalium" % "kalium" % "0.4.0" 
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0-M2" % Test
