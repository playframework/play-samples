name := "play-java-streaming-example"

version := "1.0-SNAPSHOT"

crossScalaVersions := Seq("2.13.10", "3.3.0-RC3")
scalaVersion := crossScalaVersions.value.head

lazy val root = (project in file(".")).enablePlugins(PlayJava)

libraryDependencies += guice

// "-encoding", "utf8", "-deprecation" get set by Play automatically
scalacOptions ++= List("-Werror")
javacOptions ++= Seq(
  "-Xlint:unchecked",
  "-Xlint:deprecation",
  "-Werror"
)
