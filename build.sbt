name := """play-kalium"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies += "org.abstractj.kalium" % "kalium" % "0.4.0" 
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
