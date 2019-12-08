name := """play-scala-secure-session-example"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.10"

libraryDependencies += ws
libraryDependencies += guice
libraryDependencies += "org.abstractj.kalium" % "kalium" % "0.6.0"
libraryDependencies += "com.typesafe.akka" %% "akka-distributed-data" % "2.5.26"

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.3" % Test
