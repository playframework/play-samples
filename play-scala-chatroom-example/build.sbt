import Dependencies.commonDeps
import Dependencies.scala2AkkaDeps
import Dependencies.scala3AkkaDeps

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
        case Some((3, n)) =>  (commonDeps ++ scala3AkkaDeps).map { dep =>
            dep.excludeAll(
              ExclusionRule("org.scala-lang.modules", "scala-xml_2.13"),
              ExclusionRule("com.typesafe.play", "twirl-api_2.13"),
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
