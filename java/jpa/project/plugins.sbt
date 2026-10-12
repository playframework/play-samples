// The Play plugin
resolvers ++= Seq(Resolver.sonatypeCentralSnapshots, Resolver.ApacheMavenSnapshotsRepo)
addSbtPlugin("org.playframework" % "sbt-plugin" % "3.1.0-M10-3968a052-SNAPSHOT")

// Web plugins
addSbtPlugin("com.github.sbt" % "sbt-coffeescript" % "2.1.0-M3+27-0022b9fb-SNAPSHOT")
addSbtPlugin("com.github.sbt" % "sbt-less" % "2.1.0-M2+35-40e90dfc-SNAPSHOT")
addSbtPlugin("com.github.sbt" % "sbt-jshint" % "2.1.0-M1+33-22eb7a8a-SNAPSHOT")
addSbtPlugin("com.github.sbt" % "sbt-rjs" % "2.1.0-M1+33-d0d08f2d-SNAPSHOT")
addSbtPlugin("com.github.sbt" % "sbt-digest" % "2.2.0-M1+35-897604da-SNAPSHOT")
addSbtPlugin("com.github.sbt" % "sbt-mocha" % "2.2.0-M3+18-843576a8-SNAPSHOT")
