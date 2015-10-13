import sbt.{Resolver, AutoPlugin}
import sbt.plugins.JvmPlugin
import sbt.Keys._
import sbt._

object Common extends AutoPlugin {
  override def trigger = allRequirements
  override def requires = JvmPlugin

  override def projectSettings = Seq(
    scalaVersion := "2.11.7",
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
    scalacOptions ++= Seq(
      "-encoding", "UTF-8", // yes, this is 2 args
      "-deprecation",
      "-feature",
      "-unchecked",
      "-Xlint",
      "-Yno-adapted-args",
      "-Ywarn-numeric-widen",
      "-Xfatal-warnings"
    ),
    libraryDependencies ++= Seq(
      "javax.inject" % "javax.inject" % "1",
      "com.google.inject" % "guice" % "4.0"
    ),
    scalacOptions in Test ++= Seq("-Yrangepos")
  )
}

