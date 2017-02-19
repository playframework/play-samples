name := "play-ebean-example"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.12.1"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)
  
libraryDependencies += guice
libraryDependencies += jdbc
libraryDependencies += "com.h2database" % "h2" % "1.4.191" % Test
