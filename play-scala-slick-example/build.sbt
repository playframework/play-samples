lazy val root = (project in file("."))
  .settings(
    name := "play-scala-slick-examples",
    version := "2.8.x"
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
      scalaVersion := "2.13.8",
      scalacOptions ++= Seq(
        "-feature",
        "-deprecation",
        "-Xfatal-warnings"
      ),
      libraryDependencies ++= Seq(
        guice,
        "com.typesafe.play" %% "play-slick" % "5.0.0",
        "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0",
        "com.h2database" % "h2" % "1.4.200",
        specs2 % Test,
      ),
      concurrentRestrictions in Global += Tags.limit(Tags.Test, 1)
    )
    .settings(javaOptions in Test += "-Dslick.dbs.default.connectionTimeout=30 seconds")

lazy val computerDatabaseSample = sampleProject("computer-database")

lazy val basicSample = sampleProject("basic")

lazy val personSample = sampleProject("person")
