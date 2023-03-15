import play.core.PlayVersion.akkaVersion

lazy val scala213 = "2.13.10"
lazy val scala33 = "3.3.0-RC3"
lazy val supportedScalaVersion = List(scala213, scala33)

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """play-scala-chatroom-example""",
    version := "1.0-SNAPSHOT",
    scalaVersion := scala33,
    crossScalaVersions := supportedScalaVersion,
    libraryDependencies ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((3, n)) =>  Seq(
          guice,
          "org.webjars" %% "webjars-play" % "2.8.18",
          "org.webjars" % "flot" % "0.8.3-1",
          "org.webjars" % "bootstrap" % "3.3.7-1",
          "net.logstash.logback" % "logstash-logback-encoder" % "6.6",
          "org.jsoup" % "jsoup" % "1.15.4",
          "ch.qos.logback" % "logback-classic" % "1.4.5",
          ("com.typesafe.akka" %% "akka-slf4j" % akkaVersion).cross(CrossVersion.for3Use2_13),
          ("com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test).cross(CrossVersion.for3Use2_13),
          ("com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test).cross(CrossVersion.for3Use2_13),
          "org.scalatestplus.play" %% "scalatestplus-play" % "6.0.0-M2+0-d4697b31+20230227-1643-SNAPSHOT" % Test,
        ).map { dep =>
            dep.excludeAll(
              ExclusionRule("org.scala-lang.modules", "scala-xml_2.13"),
              ExclusionRule("com.typesafe.play", "twirl-api_2.13"),
              ExclusionRule("com.typesafe", "ssl-config-core_2.13"),
            )
          }
        case _        =>  Seq(
          guice,
          "org.webjars" %% "webjars-play" % "2.8.18",
          "org.webjars" % "flot" % "0.8.3-1",
          "org.webjars" % "bootstrap" % "3.3.7-1",
          "net.logstash.logback" % "logstash-logback-encoder" % "6.3",
          "org.jsoup" % "jsoup" % "1.15.4",
          "ch.qos.logback" % "logback-classic" % "1.2.11",
          "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
          "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
          "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
          "org.scalatestplus.play" %% "scalatestplus-play" % "6.0.0-M2" % Test
        )
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
