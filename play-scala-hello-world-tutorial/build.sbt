lazy val scala213 = "2.13.10"
lazy val scala3 = "3.3.0-RC3"

lazy val core = Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "6.0.0-M2" % Test,
)

lazy val scala3Deps = Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "6.0.0-M2+0-d4697b31+20230227-1631-SNAPSHOT" % Test,
)

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """play-scala-hello-world-tutorial""",
    organization := "com.example",
    version := "1.0-SNAPSHOT",
    scalaVersion := scala213,
    crossScalaVersions := Seq(scala213, scala3),
    libraryDependencies ++= Seq(
      guice,
    ),
    libraryDependencies ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((3, _)) => scala3Deps
        case _ => core
      }
    },
    scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
          case Some((2, _)) =>
            Seq(
              "-feature",
              "-Xfatal-warnings",
              "-Xsource:3",
            )
          case _ => Nil
      }
    }
  )
