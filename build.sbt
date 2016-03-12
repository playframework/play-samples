name := """play-slick-3.0"""

version := "1.1-SNAPSHOT"

scalaVersion in ThisBuild := "2.11.7"

initialize := {
  val _ = initialize.value
  if (sys.props("java.specification.version") != "1.8")
    sys.error("Java 8 is required for this project.")
}

lazy val flyway = (project in file("modules/flyway"))
  .enablePlugins(FlywayPlugin)

lazy val api = (project in file("modules/api"))
  .enablePlugins(Common)

lazy val slick = (project in file("modules/slick"))
  .enablePlugins(Common)
  .aggregate(api)
  .dependsOn(api)

lazy val play = (project in file("modules/play"))
  .enablePlugins(PlayScala)
  .aggregate(api, slick)
  .dependsOn(api, slick)

