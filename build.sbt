name := """play-java-chatroom-example"""

version := "2.6.x"

scalaVersion := "2.12.4"

crossScalaVersions := Seq("2.11.12", "2.12.4")

lazy val root = (project in file(".")).enablePlugins(PlayJava)

libraryDependencies += "org.webjars" %% "webjars-play" % "2.6.2"
libraryDependencies += "org.webjars" % "flot" % "0.8.3"
libraryDependencies += "org.webjars" % "bootstrap" % "3.3.6"

libraryDependencies += guice
libraryDependencies += ws

libraryDependencies += "org.assertj" % "assertj-core" % "3.8.0" % Test
libraryDependencies += "org.awaitility" % "awaitility" % "3.0.0" % Test

// Needed to make JUnit report the tests being run
testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))
