lazy val root = (project in file("."))
  .enablePlugins(PlayJava)
  .settings(
    name := "play-java-rest-api-example",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.13.12",
    libraryDependencies ++= Seq(
      guice,
      javaJpa,
      "com.h2database" % "h2" % "1.4.200",
      "org.hibernate" % "hibernate-core" % "5.6.15.Final",
      "io.dropwizard.metrics" % "metrics-core" % "4.2.19",
      "com.palominolabs.http" % "url-builder" % "1.1.5",
      "net.jodah" % "failsafe" % "2.4.4",
    ),
    PlayKeys.externalizeResources := false,
    (Test / testOptions) := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v")),
    javacOptions ++= Seq(
      "-Xlint:unchecked",
      "-Xlint:deprecation",
      "-Werror"
    )
  )

val gatlingVersion = "3.3.1"
lazy val gatling = (project in file("gatling"))
  .enablePlugins(GatlingPlugin)
  .settings(
    scalaVersion := "2.12.18",
    libraryDependencies ++= Seq(
      "io.gatling.highcharts" % "gatling-charts-highcharts" % gatlingVersion % Test,
      "io.gatling" % "gatling-test-framework" % gatlingVersion % Test
    )
  )
