import play.core.PlayVersion.akkaVersion

lazy val scala213 = "2.13.10"
lazy val scala3 = "3.3.0-RC3"
lazy val supportedScalaVersion = List(scala213, scala3)

lazy val commonDeps = Seq(
  guice,
  "org.webjars" %% "webjars-play" % "2.9.0-M3",
  "org.webjars" % "flot" % "0.8.3-1",
  "org.webjars" % "bootstrap" % "3.3.7-1",
  "net.logstash.logback" % "logstash-logback-encoder" % "6.6",
  "org.jsoup" % "jsoup" % "1.15.4",
  "ch.qos.logback" % "logback-classic" % "1.4.6",
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
  "org.scalatestplus.play" %% "scalatestplus-play" % "6.0.0-M3-SNAPSHOT" % Test
)

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """play-scala-chatroom-example""",
    version := "1.0-SNAPSHOT",
    scalaVersion := scala3,
    crossScalaVersions := supportedScalaVersion,
    libraryDependencies ++= commonDeps,
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
