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
    organization := "com.example",
    version := "1.0-SNAPSHOT",
    javacOptions ++= Seq("--release", "11"),
    scalacOptions ++= Seq(
      "-release",
      "11", // yes, this is 2 args (could also be done as -release:11 however)
    ) ++ (CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, _)) =>
        Seq(
          "-Ywarn-numeric-widen",
        )
      case _ => Seq.empty
    }),
    scalacOptions in Test ++= (CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, _)) =>
        Seq(
          "-Yrangepos",
        )
      case _ => Seq.empty
    }),
    autoAPIMappings := true
  )
}
