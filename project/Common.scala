import sbt.Keys._
import sbt._
import sbt.plugins.JvmPlugin

/**
 * Settings that are comment to all the SBT projects
 */
object Common extends AutoPlugin {
  override def trigger = allRequirements
  override def requires: sbt.Plugins = JvmPlugin

  override def projectSettings = Seq(
    organization := "com.lightbend.catapi",
    version := "1.0-SNAPSHOT",
    resolvers += Resolver.typesafeRepo("releases"),
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
    scalacOptions ++= Seq(
      "-encoding", "UTF-8", // yes, this is 2 args
      "-target:jvm-1.8",
      "-deprecation",
      "-feature",
      "-unchecked",
      "-Xlint",
      "-Yno-adapted-args",
      "-Ywarn-numeric-widen"
      //"-Xfatal-warnings"
    ),
    scalaVersion := "2.11.8",
    scalacOptions in Test ++= Seq("-Yrangepos"),
    autoAPIMappings := true,
    libraryDependencies ++= Seq(
      "javax.inject" % "javax.inject" % "1",
      "org.slf4j" % "slf4j-api" % "1.7.21",
      "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
    )
  )
}
