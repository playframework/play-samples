name := "play-anorm"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.8"

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  jdbc,
  evolutions,
  "com.adrianhurt" %% "play-bootstrap" % "1.0-P25-B3",
  "com.typesafe.play" %% "anorm" % "2.5.0",
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % "test"
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)
