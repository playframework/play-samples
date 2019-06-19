val akkaVersion = "2.5.22"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

name := """play-chatroom-scala-example"""

version := "2.7.x"

scalaVersion := "2.13.0"

crossScalaVersions := Seq("2.13.0", "2.12.8")

libraryDependencies += guice

libraryDependencies += "org.webjars" %% "webjars-play" % "2.7.0"
libraryDependencies += "org.webjars" % "flot" % "0.8.3-1"
libraryDependencies += "org.webjars" % "bootstrap" % "3.3.6"

// https://mvnrepository.com/artifact/net.logstash.logback/logstash-logback-encoder
libraryDependencies += "net.logstash.logback" % "logstash-logback-encoder" % "5.2"

libraryDependencies += "org.jsoup" % "jsoup" % "1.11.3"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test
libraryDependencies += "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.2" % Test

scalacOptions ++= Seq(
    "-feature",
    "-deprecation",
    "-Xfatal-warnings"
)
