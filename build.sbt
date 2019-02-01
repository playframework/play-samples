name := """play-2.6-log4j2"""

version := "1.0-SNAPSHOT"

val log4jVersion = "2.10.0"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .disablePlugins(PlayLogback)

scalaVersion in ThisBuild := "2.12.7"

crossScalaVersions := Seq("2.11.12", "2.12.7")

libraryDependencies += guice
libraryDependencies += "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.11.1"
libraryDependencies += "org.apache.logging.log4j" % "log4j-api" % "2.11.1"
libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % "2.11.1"

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.1" % Test

javaOptions += "-Dlog4j.configurationFile=conf/log4j2.xml"

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-Xfatal-warnings"
)
