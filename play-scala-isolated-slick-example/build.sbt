import play.core.PlayVersion.{ current => playVersion }
lazy val flyway = (project in file("modules/flyway"))
  .enablePlugins(FlywayPlugin)
  .settings(Common.scalaSettings)

lazy val api = (project in file("modules/api"))
  .settings(Common.projectSettings)

lazy val slick = (project in file("modules/slick"))
  .settings(Common.projectSettings)
  .aggregate(api)
  .dependsOn(api)

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """play-isolated-slick""",
    version := "1.1-SNAPSHOT",
    scalaVersion := "2.13.0",
    TwirlKeys.templateImports += "com.example.user.User",
    libraryDependencies ++= Seq(
      guice,
      "com.h2database" % "h2" % "1.4.199",
      "org.flywaydb" % "flyway-core" % "5.2.4",
      "com.typesafe.play" %% "play-ahc-ws" % playVersion % Test,
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0-M3" % Test
    ),
    fork in Test := true
  )
  .aggregate(slick)
  .dependsOn(slick)
