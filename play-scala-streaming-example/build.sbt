name := "play-scala-streaming-example"

version := "2.7.x"

scalaVersion := "2.12.8"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies += guice

libraryDependencies += ws % Test
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.2" % Test

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-Xfatal-warnings"
)
