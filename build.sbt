name := """play-chatroom-scala-example"""

version := "2.6.x"

scalaVersion := "2.12.2"

resolvers += Resolver.sonatypeRepo("snapshots") 

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies += guice

libraryDependencies += "org.webjars" % "flot" % "0.8.3"
libraryDependencies += "org.webjars" % "bootstrap" % "3.3.6"

// https://mvnrepository.com/artifact/net.logstash.logback/logstash-logback-encoder
libraryDependencies += "net.logstash.logback" % "logstash-logback-encoder" % "4.11"

libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % "2.5.3"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0" % Test
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.5.3" % Test
libraryDependencies += "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.3" % Test

