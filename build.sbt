name := """macwire-di"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

resolvers ++= Seq(
  "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
)


libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  specs2 % Test,
  "com.softwaremill.macwire" %% "macros" % "1.0.1",
  "com.softwaremill.macwire" %% "runtime" % "1.0.1"
)

routesGenerator := play.routes.compiler.InjectedRoutesGenerator
