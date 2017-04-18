name := """fileupload"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.2"

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0-M3" % Test
libraryDependencies += ws
libraryDependencies += guice

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
