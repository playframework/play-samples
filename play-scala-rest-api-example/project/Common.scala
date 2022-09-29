import sbt.Keys._
import sbt._
import sbt.plugins.JvmPlugin

/**
  * Settings that are common to all the SBT projects
  */
object Common extends AutoPlugin {
  override def trigger = allRequirements
  override def requires: sbt.Plugins = JvmPlugin

  override def projectSettings = Seq(
    organization := "com.lightbend.restapi",
    version := "1.0-SNAPSHOT",
    resolvers += Resolver.typesafeRepo("releases"),
    javacOptions ++= Seq("--release", "11"),
    scalacOptions ++= Seq(
      "-encoding",
      "UTF-8", // yes, this is 2 args
      "-release",
      "11", // yes, this is 2 args (could also be done as -release:11 however)
      "-deprecation",
      "-feature",
      "-unchecked",
      "-Ywarn-numeric-widen",
      "-Xfatal-warnings"
    ),
    scalacOptions in Test ++= Seq("-Yrangepos"),
    autoAPIMappings := true
  )
}
