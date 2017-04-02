name := "play-scala-anorm-example"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.12.1"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies += guice
libraryDependencies += jdbc
libraryDependencies += evolutions

libraryDependencies += "com.h2database" % "h2" % "1.4.192"
libraryDependencies += "com.typesafe.play" %% "anorm" % "2.6.0-M1"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0-M2" % Test

libraryDependencies += "org.fluentlenium" % "fluentlenium-core" % "3.1.1" % Test
