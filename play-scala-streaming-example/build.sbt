name := "play-scala-streaming-example"

version := "2.7.x"

scalaVersion := "2.13.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies += guice

libraryDependencies += ws % Test
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-Xfatal-warnings"
)
