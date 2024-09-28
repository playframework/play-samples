name := "play-java-streaming-example"

version := "1.0-SNAPSHOT"

crossScalaVersions := Seq("2.13.15", "3.3.3")
scalaVersion := crossScalaVersions.value.head

lazy val root = (project in file(".")).enablePlugins(PlayJava)
  //.enablePlugins(PlayNettyServer).disablePlugins(PlayPekkoHttpServer) // uncomment to use the Netty backend

libraryDependencies += guice

// "-encoding", "utf8", "-deprecation" get set by Play automatically
scalacOptions ++= List("-Werror")
javacOptions ++= Seq(
  "-Xlint:unchecked",
  "-Xlint:deprecation",
  "-Werror"
)

TwirlKeys.templateImports ++= Seq(
  "play.mvc.Http.{ RequestHeader => JRequestHeader }",
  "views.html.helper.CSPNonce"
)
