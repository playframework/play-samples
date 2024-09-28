lazy val scala213 = "2.13.15"
lazy val scala3 = "3.3.4"

lazy val root = (project in file("."))
  .settings(
    name := "play-scala-slick-examples",
    version := "1.0-SNAPSHOT",
    scalaVersion := scala3,
    crossScalaVersions := Seq(scala213, scala3),
  )
  .aggregate(
    basicSample,
    computerDatabaseSample,
    personSample
  )

def sampleProject(name: String) =
  Project(s"$name-sample", file("samples") / name)
    .enablePlugins(PlayScala)
    //.enablePlugins(PlayNettyServer).disablePlugins(PlayPekkoHttpServer) // uncomment to use the Netty backend
    .settings(
      scalaVersion := scala3,
      crossScalaVersions := Seq(scala213, scala3),
      scalacOptions ++= Seq(
        "-feature",
        "-Werror"
      ),
      libraryDependencies ++= Seq(
        guice,
        "org.playframework" %% "play-slick" % "6.1.1",
        "org.playframework" %% "play-slick-evolutions" % "6.1.1",
        "com.h2database" % "h2" % "2.3.232",
        specs2 % Test,
      ),
      (Global / concurrentRestrictions) += Tags.limit(Tags.Test, 1)
    )
    .settings((Test / javaOptions) += "-Dslick.dbs.default.connectionTimeout=30 seconds")
    // We use a slightly different database URL for running the slick applications and testing the slick applications.
    .settings((Test / javaOptions) ++= Seq("-Dconfig.file=conf/test.conf"))

lazy val computerDatabaseSample = sampleProject("computer-database")

lazy val basicSample = sampleProject("basic")

lazy val personSample = sampleProject("person")
