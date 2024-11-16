libraryDependencies += "com.h2database" % "h2" % "2.3.232"

// Database migration
// https://github.com/flyway/flyway-sbt
addSbtPlugin("com.github.sbt" % "flyway-sbt" % "9.22.0")

// Slick code generation
// https://github.com/tototoshi/sbt-slick-codegen
addSbtPlugin("com.github.tototoshi" % "sbt-slick-codegen" % "2.2.0")

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.9.5")
