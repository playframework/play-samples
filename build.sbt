name := """macwire-di"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.2"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws
)

libraryDependencies ++= Seq(
  "com.softwaremill.macwire" %% "macros" % "0.7",
  "com.softwaremill.macwire" %% "runtime" % "0.7"
)

routesGenerator := play.routes.compiler.InjectedRoutesGenerator
