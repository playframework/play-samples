name := "play-scala-anorm-example"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.12.2"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies += guice
libraryDependencies += jdbc
libraryDependencies += evolutions

libraryDependencies += "com.h2database" % "h2" % "1.4.194"

// 2.6.0-M1 is not final yet, and 2.5.x does not run on scala 2.12
libraryDependencies += "com.typesafe.play" %% "anorm" % "2.5.3"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0-M3" % Test

libraryDependencies += "org.fluentlenium" % "fluentlenium-core" % "3.1.1" % Test
