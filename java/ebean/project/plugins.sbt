// The Play plugin
resolvers ++= Resolver.sonatypeOssRepos("snapshots")
addSbtPlugin("org.playframework" % "sbt-plugin" % "3.1.0-d1f1b9f8-SNAPSHOT")

addSbtPlugin("org.playframework" % "sbt-play-ebean" % "8.3.0")
