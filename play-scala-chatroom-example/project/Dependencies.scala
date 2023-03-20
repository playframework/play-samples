import sbt._
import Keys._
import play.core.PlayVersion.akkaVersion

object Dependencies
{
    private val guiceVersion = "5.1.0"
    private val guiceDeps = Seq(
    "com.google.inject"            % "guice"                % guiceVersion,
    "com.google.inject.extensions" % "guice-assistedinject" % guiceVersion
    )

    lazy val commonDeps = Seq(
        "org.webjars" %% "webjars-play" % "2.8.18",
        "org.webjars" % "flot" % "0.8.3-1",
        "org.webjars" % "bootstrap" % "3.3.7-1",
        "net.logstash.logback" % "logstash-logback-encoder" % "6.6",
        "org.jsoup" % "jsoup" % "1.15.4",
        "ch.qos.logback" % "logback-classic" % "1.4.5",
    ) ++ guiceDeps

    lazy val scala3AkkaDeps = Seq(
        ("com.typesafe.akka" %% "akka-slf4j" % akkaVersion).cross(CrossVersion.for3Use2_13),
        ("com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test).cross(CrossVersion.for3Use2_13),
        ("com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test).cross(CrossVersion.for3Use2_13),
        "org.scalatestplus.play" %% "scalatestplus-play" % "6.0.0-M2+0-d4697b31+20230227-1643-SNAPSHOT" % Test,
    )

    lazy val scala2AkkaDeps = Seq(
        "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
        "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
        "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
        "org.scalatestplus.play" %% "scalatestplus-play" % "6.0.0-M2" % Test
    )  
}                                                                                                                                                                                                           