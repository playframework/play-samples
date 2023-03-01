name := "play-java-streaming-example"

version := "1.0-SNAPSHOT"

scalaVersion := "3.3.0-RC3"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

libraryDependencies += guice

scalacOptions ++= List("utf8")
javacOptions ++= Seq(
  "-Xlint:unchecked",
  "-Xlint:deprecation",
  "-Werror"
)
