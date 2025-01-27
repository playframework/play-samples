// The Play plugin
resolvers ++= Resolver.sonatypeOssRepos("snapshots")
addSbtPlugin("org.playframework" % "sbt-plugin" % "3.1.0-2c77ebef-SNAPSHOT")

addSbtPlugin("org.playframework" % "sbt-play-ebean" % "9.0.0-M1")
