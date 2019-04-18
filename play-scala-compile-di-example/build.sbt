name := """play-scala-compile-di-example"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.8"

ThisBuild / javacOptions ++= List(
  "-Xlint:unchecked",
  "-Xlint:deprecation",
)

ThisBuild / scalacOptions ++= List(
  "-encoding", "utf8",
  "-deprecation",
  "-feature",
  "-unchecked",
)

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.1" % Test
