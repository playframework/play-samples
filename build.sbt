name := """play-scala-secure-session-example"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.6"

crossScalaVersions := Seq("2.11.12", "2.12.4")

libraryDependencies += guice
libraryDependencies += "org.abstractj.kalium" % "kalium" % "0.6.0"
libraryDependencies += "com.typesafe.akka" %% "akka-distributed-data" % "2.5.8"

libraryDependencies += "com.typesafe.play" %% "play-ahc-ws" % "2.6.9" % Test
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
