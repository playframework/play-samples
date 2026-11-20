libraryDependencies += "com.h2database" % "h2" % "2.5.252"

// Database migration
// https://github.com/flyway/flyway-sbt
addSbtPlugin("com.github.sbt" % "flyway-sbt" % "13.0.0")

// Slick code generation
// https://github.com/sbt/sbt-slick-codegen
addSbtPlugin("com.github.sbt" % "sbt-slick-codegen" % "2.2.0+30-eea7f36f-SNAPSHOT")

// The Play plugin
resolvers += Resolver.sonatypeCentralSnapshots
addSbtPlugin("org.playframework" % "sbt-plugin" % "3.1.0-M10-5da0b97a-SNAPSHOT")
