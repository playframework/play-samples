name := """play-scala-macwire-di-example"""

version := "2.6.x"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.10"

crossScalaVersions := Seq("2.11.12", "2.12.10")

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.3" % Test
libraryDependencies += "com.softwaremill.macwire" %% "macros" % "2.3.1" % "provided"
