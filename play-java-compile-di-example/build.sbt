name := """play-java-compile-di-example"""

version := "1.0-SNAPSHOT"

lazy val scala213 = "2.13.10"
lazy val scala3 = "3.3.0-RC3"
lazy val supportedScalaVersion = List(scala213, scala3)

lazy val root = (project in file(".")).enablePlugins(PlayJava).settings(
    crossScalaVersions := supportedScalaVersion
  )

scalaVersion := scala3

ThisBuild / scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((3, n))   =>  List("-encoding", "utf8", "-feature", "-unchecked")
        case _              =>  List("-encoding", "utf8", "-deprecation", "-feature", "-unchecked") 
      }
    }
ThisBuild / javacOptions ++= List("-Xlint:unchecked", "-Xlint:deprecation", "-Werror")

Test / testOptions := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))