name := "play-streaming-scala"

version := "1.0-SNAPSHOT"

scalaVersion := "2.12.2"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies += guice

libraryDependencies += "com.typesafe.play" %% "play-ahc-ws" % "2.6.0-M4" % Test
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0-M2" % Test
