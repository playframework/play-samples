name := """play-2.5-log4j2"""

version := "1.0-SNAPSHOT"

// Run with activator -Dlog4j.configurationFile=conf/log4j2.xml
lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .disablePlugins(PlayLogback)
  .settings(
  libraryDependencies ++= Seq(
    "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.4.1",
    "org.apache.logging.log4j" % "log4j-api" % "2.4.1",
    "org.apache.logging.log4j" % "log4j-core" % "2.4.1"
  )
)

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0-RC1" % Test
)
