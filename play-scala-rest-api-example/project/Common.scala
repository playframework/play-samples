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
    scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, _)) =>
          Seq(
            "-Ywarn-numeric-widen",
            "-Xsource:3",
            "-encoding",
            "UTF-8", // yes, this is 2 args
            "-release",
            "11", // yes, this is 2 args (could also be done as -release:11 however)
            "-deprecation",
            "-feature",
            "-unchecked",
            "-Xfatal-warnings",
          )
        case _ => Nil
      }
    },
    autoAPIMappings := true
  )
}
