name := """play-java-hello-world-tutorial"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)
  //.enablePlugins(PlayNettyServer).disablePlugins(PlayPekkoHttpServer) // uncomment to use the Netty backend

crossScalaVersions := Seq("2.13.14", "3.3.3")

scalaVersion := crossScalaVersions.value.head

libraryDependencies += guice
