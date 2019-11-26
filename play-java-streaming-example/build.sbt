name := "play-java-streaming-example"

version := "1.0-SNAPSHOT"

scalaVersion := "2.13.1"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

libraryDependencies += guice

scalacOptions ++= List("-encoding", "utf8", "-Xfatal-warnings", "-deprecation")
javacOptions ++= Seq(
  "-Xlint:unchecked",
  "-Xlint:deprecation",
  "-Werror"
)
