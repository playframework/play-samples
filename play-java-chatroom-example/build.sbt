name := """play-java-chatroom-example"""

version := "2.7.x"

scalaVersion := "2.13.0"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

libraryDependencies += "org.webjars" %% "webjars-play" % "2.7.3"
libraryDependencies += "org.webjars" % "flot" % "0.8.3"
libraryDependencies += "org.webjars" % "bootstrap" % "3.3.6"

libraryDependencies += guice
libraryDependencies += ws

libraryDependencies += "org.assertj" % "assertj-core" % "3.8.0" % Test
libraryDependencies += "org.awaitility" % "awaitility" % "3.0.0" % Test

// Needed to make JUnit report the tests being run
testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))

javacOptions ++= Seq(
  "-Xlint:unchecked",
  "-Xlint:deprecation"
)
