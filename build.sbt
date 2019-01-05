name := """play-scala-secure-session-example"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.8"

libraryDependencies += ws
libraryDependencies += guice
libraryDependencies += "org.abstractj.kalium" % "kalium" % "0.8.0"
libraryDependencies += "com.typesafe.akka" %% "akka-distributed-data" % "2.5.18"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.0-RC2" % Test

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-Xfatal-warnings"
)
