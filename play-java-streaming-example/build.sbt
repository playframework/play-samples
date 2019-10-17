name := "play-java-streaming-example"

version := "1.0-SNAPSHOT"

scalaVersion := "2.12.10"

crossScalaVersions := Seq("2.11.12", "2.12.10")

lazy val root = (project in file(".")).enablePlugins(PlayJava)

libraryDependencies += guice
