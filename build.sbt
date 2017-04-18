name := "play-java-streaming-example"

version := "1.0-SNAPSHOT"

scalaVersion := "2.12.2"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

libraryDependencies += guice
