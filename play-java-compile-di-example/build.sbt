name := """play-java-compile-di-example"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "3.3.0-RC3"

ThisBuild / scalacOptions ++= List("-encoding", "utf8", "-feature", "-unchecked")
ThisBuild / javacOptions ++= List("-Xlint:unchecked", "-Xlint:deprecation", "-Werror")

Test / testOptions := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))