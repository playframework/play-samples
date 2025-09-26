// The Play plugin
resolvers += Resolver.sonatypeCentralSnapshots
addSbtPlugin("org.playframework" % "sbt-plugin" % "3.1.0-M3")

// Web plugins
addSbtPlugin("com.github.sbt" % "sbt-coffeescript" % "2.0.1")
addSbtPlugin("com.github.sbt" % "sbt-less" % "2.0.1")
addSbtPlugin("com.github.sbt" % "sbt-jshint" % "2.0.1")
addSbtPlugin("com.github.sbt" % "sbt-rjs" % "2.0.0")
addSbtPlugin("com.github.sbt" % "sbt-digest" % "2.1.0")
addSbtPlugin("com.github.sbt" % "sbt-mocha" % "2.1.0")
