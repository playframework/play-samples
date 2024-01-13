lazy val root = (project in file("."))
  .enablePlugins(PlayJava)
  .settings(
    name := """play-java-jpa-example""",
    version := "1.0-SNAPSHOT",
    crossScalaVersions := Seq("2.13.12", "3.3.1"),
    scalaVersion := crossScalaVersions.value.head,
    libraryDependencies ++= Seq(
      guice,
      javaJpa,
      "com.h2database" % "h2" % "2.2.224",
      "org.hibernate" % "hibernate-core" % "6.3.2.Final",
      javaWs % "test",
      "org.awaitility" % "awaitility" % "4.2.0" % "test",
      "org.assertj" % "assertj-core" % "3.25.1" % "test",
      "org.mockito" % "mockito-core" % "5.8.0" % "test",
    ),
    Test / testOptions += Tests.Argument(TestFrameworks.JUnit, "-a", "-v"),
    scalacOptions ++= List("-feature", "-Werror"),
    javacOptions ++= List("-Xlint:unchecked", "-Xlint:deprecation", "-Werror"),
    PlayKeys.externalizeResourcesExcludes += baseDirectory.value / "conf" / "META-INF" / "persistence.xml"
  )
