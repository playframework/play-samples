lazy val root = (project in file("."))
  .settings(
    name := "play-scala-slick-examples",
    version := "1.0-SNAPSHOT",
  )
  .aggregate(
    basicSample,
    computerDatabaseSample,
    personSample
  )

def sampleProject(name: String) =
  Project(s"$name-sample", file("samples") / name)
    .enablePlugins(PlayScala)
    .settings(
      scalaVersion := "2.13.12",
      scalacOptions ++= Seq(
        "-feature",
        "-deprecation",
        "-Xfatal-warnings"
      ),
      libraryDependencies ++= Seq(
        guice,
        "com.typesafe.play" %% "play-slick" % "5.1.0",
        "com.typesafe.play" %% "play-slick-evolutions" % "5.1.0",
        "com.h2database" % "h2" % "2.2.224",
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
