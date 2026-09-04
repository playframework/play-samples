import com.github.tototoshi.sbt.slick.CodegenPlugin.autoImport.{slickCodegenDatabasePassword, slickCodegenDatabaseUrl, slickCodegenJdbcDriver}
import _root_.slick.codegen.SourceCodeGenerator
import _root_.slick.{model => m}

lazy val databaseUrl = sys.env.getOrElse("DB_DEFAULT_URL", "jdbc:h2:./test")
lazy val databaseUser = sys.env.getOrElse("DB_DEFAULT_USER", "sa")
lazy val databasePassword = sys.env.getOrElse("DB_DEFAULT_PASSWORD", "")

val FlywayVersion = "13.4.0"

(ThisBuild / version) := "1.1-SNAPSHOT"

(ThisBuild / libraryDependencies) ++= Seq(
  "javax.inject" % "javax.inject" % "1",
  "com.google.inject" % "guice" % "6.0.0"
)

(ThisBuild / crossScalaVersions) := Seq("3.9.0", "3.3.8")
(ThisBuild / scalaVersion) := crossScalaVersions.value.head
(ThisBuild / scalacOptions) ++= Seq(
  "-encoding", "UTF-8", // yes, this is 2 args
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Xlint",
  "-Ywarn-numeric-widen"
)
(ThisBuild / javacOptions) ++= Seq("--release", "11")

lazy val flyway = (project in file("modules/flyway"))
  .enablePlugins(FlywayPlugin)
  .settings(
    libraryDependencies += ("org.flywaydb" % "flyway-core" % FlywayVersion).excludeAll(
      // Note: Jackson 3 Databind still depends on Jackson 2 Annotations. We do not exclude the later however,
      // because Jackson 2 Annotations hardly change. Even if Play or one of its component depend on a
      // different jackson-databind version, that shouldn't really make a problem.
      // https://repo1.maven.org/maven2/tools/jackson/core/jackson-databind/3.2.1/jackson-databind-3.2.1.pom
      // https://github.com/FasterXML/jackson-databind/blob/jackson-databind-3.2.1/pom.xml#L92-L94
      // --
      //ExclusionRule("tools.jackson.core"), // Once Play switches to Jackson 3 and we want to avoid version clashes
    ),
    flywayLocations := Seq("classpath:db/migration"),
    flywayUrl := databaseUrl,
    flywayUser := databaseUser,
    flywayPassword := databasePassword,
    flywayBaselineOnMigrate := true
  )

lazy val api = (project in file("modules/api"))


lazy val slick = (project in file("modules/slick"))
  .enablePlugins(CodegenPlugin)
  .settings(
    libraryDependencies ++= Seq(
      "com.zaxxer" % "HikariCP" % "7.1.0",
      "com.typesafe.slick" %% "slick" % "3.6.1",
      "com.typesafe.slick" %% "slick-hikaricp" % "3.6.1"
    ),

    slickCodegenDatabaseUrl := databaseUrl,
    slickCodegenDatabaseUser := databaseUser,
    slickCodegenDatabasePassword := databasePassword,
    slickCodegenDriver := _root_.slick.jdbc.H2Profile,
    slickCodegenJdbcDriver := "org.h2.Driver",
    slickCodegenOutputPackage := "com.example.user.slick",
    slickCodegenExcludedTables := Seq("schema_version"),

    slickCodegenCodeGenerator := { (model: m.Model) =>
      new SourceCodeGenerator(model) {
        override def Table = new Table(_) {
          override def Column = new Column(_) {
            override def rawType = this.model.tpe match {
              case "java.sql.Timestamp" => "java.time.Instant" // kill j.s.Timestamp
              case _ =>
                super.rawType
            }
          }
        }
      }
    },
    (Compile / sourceGenerators) += slickCodegen.taskValue
  )
  .aggregate(api)
  .dependsOn(api)

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  //.enablePlugins(PlayNettyServer).disablePlugins(PlayAkkaHttpServer) // uncomment to use the Netty backend
  .settings(
    name := """play-scala-isolated-slick-example""",
    TwirlKeys.templateImports += "com.example.user.User",
    libraryDependencies ++= Seq(
      guice,
      "com.h2database" % "h2" % "2.4.240",
      ws % Test,
      ("org.flywaydb" % "flyway-core" % FlywayVersion % Test).excludeAll(
        // Note: Jackson 3 Databind still depends on Jackson 2 Annotations. We do not exclude the later however,
        // because Jackson 2 Annotations hardly change. Even if Play or one of its component depend on a
        // different jackson-databind version, that shouldn't really make a problem.
        // https://repo1.maven.org/maven2/tools/jackson/core/jackson-databind/3.2.1/jackson-databind-3.2.1.pom
        // https://github.com/FasterXML/jackson-databind/blob/jackson-databind-3.2.1/pom.xml#L92-L94
        // --
        //ExclusionRule("tools.jackson.core"), // Once Play switches to Jackson 3 and we want to avoid version clashes
      ),
      "org.scalatestplus.play" %% "scalatestplus-play" % "6.0.2" % Test
    ),
    (Test / fork) := true
  )
  .aggregate(slick)
  .dependsOn(slick)
