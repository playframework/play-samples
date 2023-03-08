import play.core.PlayVersion.akkaVersion

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """play-scala-secure-session-example""",
    version := "1.0-SNAPSHOT",
    scalaVersion := "3.3.0-RC3",
    libraryDependencies ++= Seq(
      ws,
      guice,
      "org.abstractj.kalium" % "kalium" % "0.8.0",
      "com.typesafe" %% "ssl-config-core" % "0.6.1",
      ("com.typesafe.akka" %% "akka-distributed-data" % akkaVersion).cross(CrossVersion.for3Use2_13),
      ("com.typesafe.akka" %% "akka-cluster-typed" % akkaVersion).cross(CrossVersion.for3Use2_13),
      "org.scalatestplus.play" %% "scalatestplus-play" % "6.0.0-M2+0-d4697b31+20230227-1643-SNAPSHOT" % Test
    ),
    scalacOptions ++= Seq(
      "-feature",
      "-Xfatal-warnings",
      "-explain"
    ),
    libraryDependencies ~= { libs =>
      libs.map { dep =>
        dep.excludeAll(
          ExclusionRule("com.typesafe", "ssl-config-core_2.13"),
        )
      }
    }
  )
