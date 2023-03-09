import play.core.PlayVersion.akkaVersion

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """play-scala-chatroom-example""",
    version := "1.0-SNAPSHOT",
    scalaVersion := "3.3.0-RC3",
    libraryDependencies ++= Seq(
      guice,
      "org.webjars" %% "webjars-play" % "2.8.18",
      "org.webjars" % "flot" % "0.8.3-1",
      "org.webjars" % "bootstrap" % "3.3.7-1",
      "net.logstash.logback" % "logstash-logback-encoder" % "6.6",
      "org.jsoup" % "jsoup" % "1.12.2",
      "ch.qos.logback" % "logback-classic" % "1.4.5",
      ("com.typesafe.akka" %% "akka-slf4j" % akkaVersion).cross(CrossVersion.for3Use2_13),
      ("com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test).cross(CrossVersion.for3Use2_13),
      ("com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test).cross(CrossVersion.for3Use2_13),
      "org.scalatestplus.play" %% "scalatestplus-play" % "6.0.0-M2+0-d4697b31+20230227-1643-SNAPSHOT" % Test,
    ),
    scalacOptions ++= Seq(
      "-feature",
      "-Xfatal-warnings",
      "-explain",
      "-rewrite",
      "-source:3.0-migration"
    ),
    libraryDependencies ~= { libs =>
      libs.map { dep =>
        dep.excludeAll(
          ExclusionRule("org.scala-lang.modules", "scala-xml_2.13"),
          ExclusionRule("com.typesafe.play", "twirl-api_2.13"),
          ExclusionRule("com.typesafe", "ssl-config-core_2.13"),
        )
      }
    }
  )
