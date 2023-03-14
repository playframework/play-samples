lazy val scala213 = "2.13.10"
lazy val scala33 = "3.3.0-RC3"
lazy val supportedScalaVersion = List(scala213, scala33)

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """play-scala-compile-di-example""",
    version := "1.0-SNAPSHOT",
    scalaVersion := scala33,
    crossScalaVersions := supportedScalaVersion,
    libraryDependencies += {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((3, n))   =>  "org.scalatestplus.play" %% "scalatestplus-play" % "6.0.0-M2+0-d4697b31+20230227-1643-SNAPSHOT" % Test
        case _              =>  "org.scalatestplus.play" %% "scalatestplus-play" % "6.0.0-M2" % Test
      }
    },
    scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((3, n))   =>  List(
                                  "utf8",
                                  "-feature",
                                  "-Xfatal-warnings",
                                  "-source:3.0-migration",
                                  "-explain"
                                )
        case _              =>  List(
                                  "-encoding", "utf8",
                                  "-deprecation",
                                  "-feature",
                                  "-unchecked",
                                  "-Xfatal-warnings"
                                )
      }
    },
    // Needed for ssl-config to create self signed certificated under Java 17
    Test / javaOptions ++= List("--add-exports=java.base/sun.security.x509=ALL-UNNAMED"),
  )
