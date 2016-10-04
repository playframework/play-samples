name := """play-java"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  guice,
  javaJdbc,
  cache,
  javaWs,
  "com.h2database" % "h2" % "1.4.191" % Test
)
