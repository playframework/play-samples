name := """play-java-hello-world-web"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava /*, PlayDocsPlugin*/)

scalaVersion := "2.12.6"

libraryDependencies += guice

// https://mvnrepository.com/artifact/com.typesafe.play/play-docs

// libraryDependencies += "com.typesafe.play" %% "play-docs" % "2.6.18"

