import slick.codegen.SourceCodeGenerator
import slick.{ model => m }

libraryDependencies ++= Seq(
  "com.zaxxer" % "HikariCP" % "2.6.1",
  "com.typesafe.slick" %% "slick" % "3.2.0",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.2.0",
  "com.github.tototoshi" %% "slick-joda-mapper" % "2.3.0"
)

lazy val databaseUrl = sys.env.getOrElse("DB_DEFAULT_URL", "jdbc:h2:./test")
lazy val databaseUser = sys.env.getOrElse("DB_DEFAULT_USER", "sa")
lazy val databasePassword = sys.env.getOrElse("DB_DEFAULT_PASSWORD", "")

slickCodegenSettings
slickCodegenDatabaseUrl := databaseUrl
slickCodegenDatabaseUser := databaseUser
slickCodegenDatabasePassword := databasePassword
slickCodegenDriver := slick.driver.H2Driver
slickCodegenJdbcDriver := "org.h2.Driver"
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

sourceGenerators in Compile += slickCodegen.taskValue
