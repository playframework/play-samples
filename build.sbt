name := """play-scala-compile-di-example"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.8"

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.0-RC1" % Test
