name := """fileupload"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "1.6.0-SNAPSHOT" % Test
libraryDependencies += ws
libraryDependencies += guice

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
