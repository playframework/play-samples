name := "computer-database-java"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  jdbc,
  javaEbean,
  "org.webjars" % "jquery" % "2.1.1",
  "org.webjars" % "bootstrap" % "3.1.1",
  "org.webjars" % "font-awesome" % "4.1.0"
)     

lazy val root = (project in file(".")).enablePlugins(PlayJava)
