// The Play plugin
resolvers ++= Resolver.sonatypeOssRepos("snapshots")
addSbtPlugin("org.playframework" % "sbt-plugin" % "3.1.0-2c77ebef-SNAPSHOT")

// Web plugins
addSbtPlugin("com.github.sbt" % "sbt-coffeescript" % "2.0.1")
addSbtPlugin("com.github.sbt" % "sbt-less" % "2.0.1")
addSbtPlugin("com.github.sbt" % "sbt-jshint" % "2.0.1")
addSbtPlugin("com.github.sbt" % "sbt-rjs" % "2.0.0")
addSbtPlugin("com.github.sbt" % "sbt-digest" % "2.0.0")
addSbtPlugin("com.github.sbt" % "sbt-mocha" % "2.1.0")
