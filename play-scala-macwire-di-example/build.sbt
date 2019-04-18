name := """play-scala-macwire-di-example"""

version := "2.6.x"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.8"

crossScalaVersions := Seq("2.11.12", "2.12.7")

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.1" % Test
libraryDependencies += "com.softwaremill.macwire" %% "macros" % "2.3.1" % "provided"

scalacOptions ++= Seq(
    "-feature",
    "-deprecation",
    "-Xfatal-warnings"
)
