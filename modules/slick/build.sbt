

libraryDependencies ++= Seq(
  "org.postgresql" % "postgresql" % "9.4-1204-jdbc42",
  "com.zaxxer" % "HikariCP" % "2.4.1",
  "com.typesafe.slick" %% "slick" % "3.1.0",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.1.0",
  "com.github.tminglei" %% "slick-pg" % "0.10.0",
  "com.typesafe.play" %% "play-json" % "2.4.2", // standalone play json
  "com.github.tminglei" %% "slick-pg_play-json" % "0.10.0" // play json to postgres json
)

// Database Migrations:
// run with "sbt flywayMigrate"
// http://flywaydb.org/getstarted/firststeps/sbt.html

seq(flywaySettings: _*)

flywayUrl := "jdbc:postgresql://localhost:5432/myapp"

flywayUser := "myapp"
