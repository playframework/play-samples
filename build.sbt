name := """play-chatroom-scala"""

version := "1.0-SNAPSHOT"

scalaVersion := "2.12.1"

resolvers += Resolver.sonatypeRepo("snapshots") 

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies += guice

libraryDependencies += "org.webjars" % "flot" % "0.8.3"
libraryDependencies += "org.webjars" % "bootstrap" % "3.3.6"

lazy val akkaVersion = "2.4.11"
libraryDependencies += "com.typesafe.akka" %% "akka-http-core" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-http-experimental" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % akkaVersion

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0-M2" % Test
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test
libraryDependencies += "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test

