name := """play-java-compile-di"""

version := "1.0-SNAPSHOT"

// resolvers += Resolver.sonatypeRepo("snapshots")

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.8"

