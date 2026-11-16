// The Play plugin
resolvers ++= Seq(Resolver.sonatypeCentralSnapshots, Resolver.ApacheMavenSnapshotsRepo)
addSbtPlugin("org.playframework" % "sbt-plugin" % "3.1.0-M10-5da0b97a-SNAPSHOT")

// Web plugins
addSbtPlugin("com.github.sbt" % "sbt-coffeescript" % "2.1.0-M3+34-538e7588-SNAPSHOT")
addSbtPlugin("com.github.sbt" % "sbt-less" % "2.1.0-M2+43-6ce8a6dd-SNAPSHOT")
addSbtPlugin("com.github.sbt" % "sbt-jshint" % "2.1.0-M1+35-9f5a637e-SNAPSHOT")
addSbtPlugin("com.github.sbt" % "sbt-rjs" % "2.1.0-M1+37-f819125a-SNAPSHOT")
addSbtPlugin("com.github.sbt" % "sbt-digest" % "2.2.0-M1+39-3d6b798f-SNAPSHOT")
addSbtPlugin("com.github.sbt" % "sbt-mocha" % "2.2.0-M3+22-9cdca54f-SNAPSHOT")
