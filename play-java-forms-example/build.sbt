name := """play-java-forms-example"""

version := "2.7.x"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.0"

testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))

libraryDependencies += guice

javacOptions ++= Seq(
  "-Xlint:unchecked",
  "-Xlint:deprecation"
) 
