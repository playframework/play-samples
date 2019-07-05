import sbt.Keys._
import sbt.{Resolver, _}

object Common {

  def scalaSettings = Seq(
    scalaVersion := "2.13.0",
    scalacOptions ++= Seq(
      "-encoding", "UTF-8", // yes, this is 2 args
      "-deprecation",
      "-feature",
      "-unchecked",
      "-Xlint",
      "-Ywarn-numeric-widen"
    ),
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
  )

  def projectSettings = scalaSettings ++ Seq(
    resolvers ++= Seq(
      "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
       Resolver.sonatypeRepo("releases"),
       Resolver.sonatypeRepo("snapshots")),
    libraryDependencies ++= Seq(
      "javax.inject" % "javax.inject" % "1",
      "joda-time" % "joda-time" % "2.10.2",
      "org.joda" % "joda-convert" % "2.2.1",
      "com.google.inject" % "guice" % "4.2.2"
    ),
    scalacOptions in Test ++= Seq("-Yrangepos")
  )
}
