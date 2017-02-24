name := """play-slick-example"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.1"

libraryDependencies += guice
libraryDependencies += "com.typesafe.play" %% "play-slick" % "3.0.0-M2"
libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "3.0.0-M2"
libraryDependencies += "com.h2database" % "h2" % "1.4.192"

libraryDependencies += specs2 % Test
  

