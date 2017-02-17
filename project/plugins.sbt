// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository
resolvers += Resolver.typesafeRepo("snapshots")

resolvers += Resolver.mavenLocal

// https://oss.sonatype.org/content/repositories/snapshots/com/typesafe/play/play_2.11/2.6.0-2016-10-10-cc467d3-SNAPSHOT/
resolvers += Resolver.sonatypeRepo("snapshots") 

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.0-M1")

addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.1.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-coffeescript" % "1.0.0")
