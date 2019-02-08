name := "play-java-websocket-example"

version := "1.0"

scalaVersion := "2.12.8"

// https://github.com/sbt/junit-interface
testOptions += Tests.Argument(TestFrameworks.JUnit, "-a", "-v")

lazy val root = (project in file(".")).enablePlugins(PlayJava)

libraryDependencies += guice
libraryDependencies += ws
libraryDependencies += "org.webjars" %% "webjars-play" % "2.7.0"
libraryDependencies += "org.webjars" % "bootstrap" % "2.3.2"
libraryDependencies += "org.webjars" % "flot" % "0.8.3"

// Testing libraries for dealing with CompletionStage...
libraryDependencies += "org.assertj" % "assertj-core" % "3.8.0" % Test
libraryDependencies += "org.awaitility" % "awaitility" % "3.0.0" % Test

LessKeys.compress := true

javacOptions ++= Seq(
  "-Xlint:unchecked",
  "-Xlint:deprecation"
)
