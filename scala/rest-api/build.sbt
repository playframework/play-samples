import sbt.Keys._
import play.sbt.PlaySettings

resolvers += Resolver.sonatypeCentralSnapshots

lazy val scala213 = "2.13.17"
lazy val scala3 = "3.3.6"

lazy val root = (project in file("."))
  .enablePlugins(PlayService, PlayLayoutPlugin, Common)
  //.enablePlugins(PlayNettyServer).disablePlugins(PlayPekkoHttpServer) // uncomment to use the Netty backend
  .settings(
    name := "play-scala-rest-api-example",
    scalaVersion := scala213,
    crossScalaVersions := Seq(scala213, scala3),
    libraryDependencies ++= Seq(
      guice,
      ("net.logstash.logback" % "logstash-logback-encoder" % "8.1")
        .excludeAll(ExclusionRule("com.fasterxml.jackson.core")), // Avoid conflicts with Play's Jackson dependency
      "com.indoorvivants" %% "scala-uri" % "4.2.0",
      "net.codingwell" %% "scala-guice" % "7.0.0",
      "org.scalatestplus.play" %% "scalatestplus-play" % "8.0.0-M2" % Test
    ),
    scalacOptions ++= Seq(
      "-feature",
      "-Werror"
    )
  )

lazy val gatlingVersion = "3.9.5"
lazy val gatling = (project in file("gatling"))
  .enablePlugins(GatlingPlugin)
  .settings(
    scalaVersion := scala213,
    crossScalaVersions := Seq(scala213, scala3),
    libraryDependencies ++= Seq(
      "io.gatling.highcharts" % "gatling-charts-highcharts" % gatlingVersion % Test,
      "io.gatling" % "gatling-test-framework" % gatlingVersion % Test
    )
  )

// Documentation for this project:
//    sbt "project docs" "~ paradox"
//    open docs/target/paradox/site/index.html
lazy val docs = (project in file("docs")).enablePlugins(ParadoxPlugin).
  settings(
    scalaVersion := scala213,
    crossScalaVersions := Seq(scala213, scala3),
    paradoxProperties += ("download_url" -> "https://example.lightbend.com/v1/download/play-samples-play-scala-rest-api-example")
  )
