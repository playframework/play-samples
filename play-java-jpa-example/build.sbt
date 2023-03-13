lazy val scala213 = "2.13.10"
lazy val scala3 = "3.3.0-RC3"

lazy val root = (project in file("."))
  .enablePlugins(PlayJava)
  .settings(
    name := """play-java-jpa-example""",
    version := "1.0-SNAPSHOT",
    scalaVersion := scala213,
    crossScalaVersions := Seq(scala213, scala3),
    libraryDependencies ++= Seq(
      guice,
      javaJpa,
      "com.h2database" % "h2" % "2.1.214",
      "org.hibernate" % "hibernate-core" % "5.6.15.Final",
      javaWs % "test",
      "org.awaitility" % "awaitility" % "4.2.0" % "test",
      "org.assertj" % "assertj-core" % "3.24.2" % "test",
      "org.mockito" % "mockito-core" % "5.2.0" % "test",
    ),
    Test / testOptions += Tests.Argument(TestFrameworks.JUnit, "-a", "-v"),
    scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, _)) =>
          Seq(
            "-feature",
            "-Xsource:3"
          )
        case _ => Nil
      }
    },
    javacOptions ++= List("-Xlint:unchecked", "-Xlint:deprecation"),
    PlayKeys.externalizeResourcesExcludes += baseDirectory.value / "conf" / "META-INF" / "persistence.xml"
  )
