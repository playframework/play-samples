
lazy val root = (project in file("."))
  .enablePlugins(PlayJava, PlayEbean)
  .settings(
    name := "play-java-ebean-example",
    version := "1.0.0-SNAPSHOT",
    scalaVersion := "2.13.0",
    libraryDependencies ++= Seq(
      guice,
      jdbc,
      "com.h2database" % "h2" % "1.4.199",
      "org.awaitility" % "awaitility" % "2.0.0" % Test,
      "org.assertj" % "assertj-core" % "3.6.2" % Test,
      "org.mockito" % "mockito-core" % "2.1.0" % Test,
      // To provide an implementation of JAXB-API, which is required by Ebean.
      "javax.xml.bind" % "jaxb-api" % "2.3.1",
      "javax.activation" % "activation" % "1.1.1",
      "org.glassfish.jaxb" % "jaxb-runtime" % "2.3.2",
    ),
    testOptions in Test += Tests.Argument(TestFrameworks.JUnit, "-a", "-v"),
    javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation", "-Werror")
  )
