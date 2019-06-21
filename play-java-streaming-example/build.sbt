name := "play-java-streaming-example"

version := "1.0-SNAPSHOT"

scalaVersion := "2.13.0"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

libraryDependencies += guice

javacOptions ++= Seq(
  "-Xlint:unchecked",
  "-Xlint:deprecation"
)
