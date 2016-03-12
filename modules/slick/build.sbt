import slick.codegen.SourceCodeGenerator
import slick.{ model => m }

libraryDependencies ++= Seq(
  "com.zaxxer" % "HikariCP" % "2.4.1",
  "com.typesafe.slick" %% "slick" % "3.1.1",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.1.1",
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  "com.github.tminglei" %% "slick-pg" % "0.12.0",
  "com.github.tminglei" %% "slick-pg_play-json" % "0.12.0",
  "com.github.tminglei" %% "slick-pg_joda-time" % "0.12.0",
  "com.github.tototoshi" %% "slick-joda-mapper" % "2.1.0"
)

lazy val databaseUrl = sys.env.getOrElse("DB_DEFAULT_URL", "jdbc:postgresql:myapp")
lazy val databaseUser = sys.env.getOrElse("DB_DEFAULT_USER", "myuser")
lazy val databasePassword = sys.env.getOrElse("DB_DEFAULT_PASSWORD", "mypass")

slickCodegenSettings
slickCodegenDatabaseUrl := databaseUrl
slickCodegenDatabaseUser := databaseUser
slickCodegenDatabasePassword := databasePassword
slickCodegenDriver := slick.driver.PostgresDriver
slickCodegenJdbcDriver := "org.postgresql.Driver"
slickCodegenOutputPackage := "com.example.user.slick"
slickCodegenExcludedTables := Seq("schema_version")

slickCodegenCodeGenerator := { (model:  m.Model) =>
  new SourceCodeGenerator(model) {
    override def code =
      "import com.github.tototoshi.slick.H2JodaSupport._\n" + "import org.joda.time.DateTime\n" + super.code
    override def Table = new Table(_) {
      override def Column = new Column(_) {
        override def rawType = model.tpe match {
          case "java.sql.Timestamp" => "DateTime" // kill j.s.Timestamp
          case _ =>
            super.rawType
        }
      }
    }
  }
}

sourceGenerators in Compile <+= slickCodegen
