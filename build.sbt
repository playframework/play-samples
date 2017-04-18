name := """play-slick-example"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.2"

libraryDependencies += guice
libraryDependencies += "com.typesafe.play" %% "play-slick" %  "3.0.0-M3"
libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "3.0.0-M3"
libraryDependencies += "com.h2database" % "h2" % "1.4.194"

libraryDependencies += specs2 % Test
  

