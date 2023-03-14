val log4jVersion = "2.17.2"
lazy val scala213 = "2.13.10"
lazy val scala33 = "3.3.0-RC3"
lazy val supportedScalaVersion = List(scala213, scala33)

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .disablePlugins(PlayLogback)
  .settings(
    name := """play-scala-log4j2-example""",
    version := "1.0-SNAPSHOT",
    scalaVersion := scala33,
    crossScalaVersions := supportedScalaVersion,
    libraryDependencies ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((3, n))   =>  Seq(
          guice,
          "org.apache.logging.log4j" % "log4j-slf4j-impl" % log4jVersion,
          "org.apache.logging.log4j" % "log4j-api" % log4jVersion,
          "org.apache.logging.log4j" % "log4j-core" % log4jVersion,
          "org.scalatestplus.play" %% "scalatestplus-play" % "6.0.0-M2+0-d4697b31+20230227-1643-SNAPSHOT" % Test,
        )
        case _              =>  Seq(
          guice,
          "org.apache.logging.log4j" % "log4j-slf4j-impl" % log4jVersion,
          "org.apache.logging.log4j" % "log4j-api" % log4jVersion,
          "org.apache.logging.log4j" % "log4j-core" % log4jVersion,
          "org.scalatestplus.play" %% "scalatestplus-play" % "6.0.0-M2" % Test
        )
      }
    },
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
    // Needed for ssl-config to create self signed certificated under Java 17
    Test / javaOptions ++= List("--add-exports=java.base/sun.security.x509=ALL-UNNAMED"),
  )
