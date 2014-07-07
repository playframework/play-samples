name := """activator-play-tls-example"""

version := "1.0.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

// We require JDK 1.8 to show usage of the latest JSSE APIs.
initialize := {
  val _ = initialize.value // run the previous initialization
  val specVersion = sys.props("java.specification.version")
  assert(specVersion.contains("1.8"), "Java 1.8.0 or above required")
}

libraryDependencies ++= Seq(
  ws
)
