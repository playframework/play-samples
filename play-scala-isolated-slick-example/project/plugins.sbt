libraryDependencies += "com.h2database" % "h2" % "1.4.200" // Can't use latest h2 currently: https://github.com/flyway/flyway-sbt/issues/82#issuecomment-1636728997

// Database migration
// https://github.com/flyway/flyway-sbt
addSbtPlugin("io.github.davidmweber" % "flyway-sbt" % "9.15.2")

// Slick code generation
// https://github.com/tototoshi/sbt-slick-codegen
addSbtPlugin("com.github.tototoshi" % "sbt-slick-codegen" % "2.0.0")

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.9.1")
