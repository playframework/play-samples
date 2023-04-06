lazy val root = (project in file("."))
  .enablePlugins(PlayJava)
  .settings(
    name := """play-java-jpa-example""",
    version := "1.0-SNAPSHOT",
    crossScalaVersions := Seq("2.13.10", "3.3.0-RC3"),
    scalaVersion := crossScalaVersions.value.head,
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
    scalacOptions ++= List("-feature", "-Werror"),
    javacOptions ++= List("-Xlint:unchecked", "-Xlint:deprecation", "-Werror"),
    PlayKeys.externalizeResourcesExcludes += baseDirectory.value / "conf" / "META-INF" / "persistence.xml"
  )
