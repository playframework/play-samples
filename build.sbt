name := """play-java"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

resolvers += Resolver.sonatypeRepo("snapshots") 

scalaVersion := "2.11.8"

libraryDependencies += guice
libraryDependencies += javaJdbc
libraryDependencies += cache
libraryDependencies += javaWs