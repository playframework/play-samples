// The Play plugin
resolvers ++= Resolver.sonatypeOssRepos("snapshots")
addSbtPlugin("org.playframework" % "sbt-plugin" % "3.1.0-4009e5a0-SNAPSHOT")

addSbtPlugin("org.playframework" % "sbt-play-ebean" % "8.3.0")
