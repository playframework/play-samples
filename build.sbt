name := """play-slick-3.0"""

version := "1.1-SNAPSHOT"

libraryDependencies ++= Seq(
  specs2 % Test
)

scalaVersion in ThisBuild := "2.11.7"

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

resolvers += Resolver.sonatypeRepo("releases")

resolvers += Resolver.sonatypeRepo("snapshots")

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

initialize := {
  val _ = initialize.value
  if (sys.props("java.specification.version") != "1.8")
    sys.error("Java 8 is required for this project.")
}

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .aggregate(
    api, slick
  ).dependsOn(api, slick)

lazy val api = (project in file("modules/api"))
  .enablePlugins(Common)

lazy val slick = (project in file("modules/slick"))
  .enablePlugins(Common)
  .dependsOn(api)
