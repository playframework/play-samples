import play.core.PlayVersion.akkaVersion
 
lazy val scala213 = "2.13.10"
lazy val scala33 = "3.3.0-RC3"
lazy val supportedScalaVersion = List(scala213, scala33)

lazy val commonDeps = Seq(
  guice,
  ws,
  "org.abstractj.kalium" % "kalium" % "0.8.0",
)

lazy val scala2AkkaDeps = Seq(
  "com.typesafe.akka" %% "akka-distributed-data" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-typed" % akkaVersion,
  "org.scalatestplus.play" %% "scalatestplus-play" % "6.0.0-M2" % Test
)

lazy val scala3AkkaDeps = Seq(
  "com.typesafe" %% "ssl-config-core" % "0.6.1",
  ("com.typesafe.akka" %% "akka-distributed-data" % akkaVersion).cross(CrossVersion.for3Use2_13),
  ("com.typesafe.akka" %% "akka-cluster-typed" % akkaVersion).cross(CrossVersion.for3Use2_13),
  "org.scalatestplus.play" %% "scalatestplus-play" % "6.0.0-M2+0-d4697b31+20230227-1643-SNAPSHOT" % Test
)

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """play-scala-secure-session-example""",
    version := "1.0-SNAPSHOT",
    scalaVersion := scala33,
    crossScalaVersions := supportedScalaVersion,

    libraryDependencies ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((3, n)) =>  (commonDeps ++ scala3AkkaDeps).map { dep =>
            dep.excludeAll(
              ExclusionRule("com.typesafe", "ssl-config-core_2.13"),
            )
          }
        case _        =>  commonDeps ++ scala2AkkaDeps
      }
    },
    scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((3, n))   =>  List(
                                  "-feature",
                                  "-Xfatal-warnings",
                                  "-explain",
                                  "-source:3.0-migration"
                                )
        case _              =>  List(
                                  "-feature",
                                  "-deprecation",
                                  "-Xfatal-warnings",
                                  "-Xsource:3"
                                )
      }
    },
  )
