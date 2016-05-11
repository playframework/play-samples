name := "reactive-stocks-java8"

version := "1.0"

scalaVersion := "2.11.8"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

libraryDependencies ++= Seq(
  javaWs,
  "org.webjars" % "bootstrap" % "2.3.2",
  "org.webjars" % "flot" % "0.8.3",
  "org.easytesting" % "fest-assert" % "1.4" % Test,
  "com.typesafe.akka" %% "akka-testkit" % "2.4.4" % Test,
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.4.4" % Test
)


LessKeys.compress := true
