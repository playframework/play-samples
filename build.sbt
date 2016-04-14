name := "computer-database-java"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.7"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean).settings {
  libraryDependencies ++= Seq(
    jdbc,
    // https://adrianhurt.github.io/play-bootstrap/
    "com.adrianhurt" %% "play-bootstrap" % "1.0-P25-B3"
  )
}
