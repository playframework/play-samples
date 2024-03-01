import play.core.PlayVersion.akkaVersion

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  //.enablePlugins(PlayNettyServer).disablePlugins(PlayAkkaHttpServer) // uncomment to use the Netty backend
  .settings(
    name := """play-scala-secure-session-example""",
    version := "1.0-SNAPSHOT",
    crossScalaVersions := Seq("2.13.13", "3.3.3"),
    scalaVersion := crossScalaVersions.value.head,
    libraryDependencies ++= Seq(
      ws,
      guice,
      "org.abstractj.kalium" % "kalium" % "0.8.0",
      ("com.typesafe.akka" %% "akka-distributed-data" % akkaVersion).cross(CrossVersion.for3Use2_13),
      ("com.typesafe.akka" %% "akka-cluster-typed" % akkaVersion).cross(CrossVersion.for3Use2_13),
      "org.scalatestplus.play" %% "scalatestplus-play" % "6.0.1" % Test
    ),
    scalacOptions ++= Seq(
      "-feature",
      "-Werror"
    )
  )
