libraryDependencies += "com.h2database" % "h2" % "1.4.196"

// Database migration
// https://github.com/flyway/flyway-sbt
addSbtPlugin("io.github.davidmweber" % "flyway-sbt" % "7.4.0")

// Slick code generation
// https://github.com/tototoshi/sbt-slick-codegen
addSbtPlugin("com.github.tototoshi" % "sbt-slick-codegen" % "2.0.0")

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.22")
