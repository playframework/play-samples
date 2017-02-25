
name := """play-isolated-slick"""

version := "1.1-SNAPSHOT"

scalaVersion in ThisBuild := "2.11.8"

initialize := {
  val _ = initialize.value
  if (sys.props("java.specification.version") != "1.8")
    sys.error("Java 8 is required for this project.")
}

lazy val flyway = (project in file("modules/flyway"))
  .enablePlugins(FlywayPlugin)

lazy val api = (project in file("modules/api"))
  .settings(Common.projectSettings)

lazy val slick = (project in file("modules/slick"))
  .settings(Common.projectSettings)
  .aggregate(api)
  .dependsOn(api)

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    libraryDependencies += guice,
    // Adding this means no explicit import in *.scala.html files
    TwirlKeys.templateImports += "com.example.user.User"
  ).aggregate(api, slick)
  .dependsOn(api, slick, flyway)

