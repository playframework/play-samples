import sbt.Keys._
import play.sbt.PlaySettings

lazy val scala213 = "2.13.15"
lazy val scala3 = "3.3.4"

lazy val root = (project in file("."))
  .enablePlugins(PlayService, PlayLayoutPlugin, Common)
  //.enablePlugins(PlayNettyServer).disablePlugins(PlayPekkoHttpServer) // uncomment to use the Netty backend
  .settings(
    name := "play-scala-rest-api-example",
    scalaVersion := scala213,
    crossScalaVersions := Seq(scala213, scala3),
    libraryDependencies ++= Seq(
      guice,
      "org.joda" % "joda-convert" % "2.2.3",
      "net.logstash.logback" % "logstash-logback-encoder" % "7.3",
      "io.lemonlabs" %% "scala-uri" % "4.0.3",
      "net.codingwell" %% "scala-guice" % "6.0.0",
      "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test
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
