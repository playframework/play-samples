name := """play-java-hello-world-tutorial"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

crossScalaVersions := Seq("2.13.11", "3.3.1")

scalaVersion := crossScalaVersions.value.head

libraryDependencies += guice
