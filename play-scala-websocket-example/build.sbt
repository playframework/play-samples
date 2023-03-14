// import play.core.PlayVersion.akkaVersion
lazy val scala213 = "2.13.10"
lazy val scala3 = "3.3.0-RC3"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := "play-scala-websocket-example",
    version := "1.0-SNAPSHOT",
    scalaVersion := scala3,
    crossScalaVersions := Seq(scala213, scala3),
    libraryDependencies ++= Seq(
      guice,
      ws,
      "org.webjars" % "flot" % "0.8.3-1",
      "org.webjars" % "bootstrap" % "3.3.7-1",
      "org.awaitility" % "awaitility" % "4.2.0" % Test,
      ("com.typesafe.akka" %% "akka-testkit" % "2.6.20" % Test).cross(CrossVersion.for3Use2_13),
      ("com.typesafe.akka" %% "akka-stream-testkit" % "2.6.20" % Test).cross(CrossVersion.for3Use2_13),
    ),
    libraryDependencies ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, _)) =>
          Seq(
            "org.scalatestplus.play" %% "scalatestplus-play" % "6.0.0-M2" % Test,
          )
        case Some((3, _)) =>
          Seq(
            "org.scalatestplus.play" %% "scalatestplus-play" % "6.0.0-M2+0-d4697b31+20230227-1631-SNAPSHOT" % Test,
          )
        case _ => Nil
      }
    },
    excludeDependencies ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((3, _)) =>
          Seq(
            ExclusionRule("com.typesafe", "ssl-config-core_2.13"),
          )
        case _ => Nil
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
        case Some((3, _)) =>
          Seq(
            // "-explain",
          )
        case _ => Nil
      }
    }
  )
