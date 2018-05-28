name := "play-java-streaming-example"

version := "1.0-SNAPSHOT"

scalaVersion := "2.12.6"

crossScalaVersions := Seq("2.11.12", "2.12.4")

lazy val root = (project in file(".")).enablePlugins(PlayJava)

libraryDependencies += guice
