name := """play-scala-compile-di-with-tests"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.1"

libraryDependencies += guice
libraryDependencies += ws
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0-M2" % Test

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
