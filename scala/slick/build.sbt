lazy val scala213 = "2.13.18"
lazy val scala3 = "3.3.9"

def scala2OnlyScalacOptions(options: String*) = Def.setting {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, _)) => options
    case _            => Seq.empty
  }
}

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

// Serialize compilation to avoid intermittent Scala 3 compiler errors across the samples.
Global / concurrentRestrictions += Tags.limit(Tags.Compile, 1)

def sampleProject(name: String) =
  Project(s"$name-sample", file("samples") / name)
    .enablePlugins(PlayScala)
    //.enablePlugins(PlayNettyServer).disablePlugins(PlayPekkoHttpServer) // uncomment to use the Netty backend
    .settings(
      resolvers += Resolver.sonatypeCentralSnapshots,
      scalaVersion := scala3,
      crossScalaVersions := Seq(scala213, scala3),
      scalacOptions ++= Seq(
        "-feature",
        "-Werror"
      ) ++ scala2OnlyScalacOptions("-Xsource:3").value,
      libraryDependencies ++= Seq(
        guice,
        "org.playframework" %% "play-slick" % "7.0.0-M1+173-17a69d28-SNAPSHOT",
        "org.playframework" %% "play-slick-evolutions" % "7.0.0-M1+173-17a69d28-SNAPSHOT",
        "com.h2database" % "h2" % "2.5.252",
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
