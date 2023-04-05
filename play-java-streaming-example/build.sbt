name := "play-java-streaming-example"

version := "1.0-SNAPSHOT"

lazy val scala213 = "2.13.10"
lazy val scala3 = "3.3.0-RC3"
lazy val supportedScalaVersion = List(scala213, scala3)

scalaVersion := scala3

lazy val root = (project in file(".")).enablePlugins(PlayJava).settings(
  crossScalaVersions := supportedScalaVersion
)

libraryDependencies += guice

scalacOptions ++= {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((3, n))   =>  Seq("utf8", "-source:3.0-migration")
    case _              =>  Seq("utf8")
  }
}
javacOptions ++= Seq(
  "-Xlint:unchecked",
  "-Xlint:deprecation",
  "-Werror"
)
