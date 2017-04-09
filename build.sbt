name := """play-scala-secure-session-example"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.1"

libraryDependencies += guice
libraryDependencies += "org.abstractj.kalium" % "kalium" % "0.4.0"

libraryDependencies += "com.typesafe.akka" %% "akka-cluster" % "2.4.17"
libraryDependencies += "com.typesafe.akka" %% "akka-distributed-data-experimental" % "2.4.17"

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0-M2" % Test
