name := """play-java-forms-example"""

version := "1.0-SNAPSHOT"

lazy val scala213 = "2.13.10"
lazy val scala3 = "3.3.0-RC3"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := scala213
crossScalaVersions := Seq(scala213, scala3)

(Test / testOptions) := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))

libraryDependencies += guice

scalacOptions ++= {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, _)) =>
      Seq(
        "-encoding",
        "utf-8",
        "-Xfatal-warnings",
        "-deprecation",
        "-Xsource:3"
      )
    case _ => Nil
  }
}
javacOptions ++= Seq(
  "-Xlint:unchecked",
  "-Xlint:deprecation",
  "-Werror"
) 
