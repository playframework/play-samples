lazy val scala213 = "2.13.10"
lazy val scala3 = "3.3.0-RC3"
lazy val supportedScalaVersion = List(scala213, scala3)

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := "play-scala-streaming-example",
    version := "1.0-SNAPSHOT",
    scalaVersion := scala3,
    crossScalaVersions := supportedScalaVersion,
    libraryDependencies ++= Seq(
      ws % Test,
      guice,
      "org.scalatestplus.play" %% "scalatestplus-play" % "6.0.0-M3-SNAPSHOT" % Test
    ),
    scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((3, n))   =>  List(
                                  "-feature",
                                  "-Xfatal-warnings",
                                  "-source:3.0-migration",
                                  "-explain"
                                )
        case _              =>  List(
                                  "-deprecation",
                                  "-feature",
                                  "-unchecked",
                                  "-Xfatal-warnings"
                                )
      }
    },
  )
