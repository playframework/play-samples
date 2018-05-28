name := """play-java-dagger2-example"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.6"

crossScalaVersions := Seq("2.11.12", "2.12.4")

libraryDependencies += ws

libraryDependencies += "com.google.dagger" % "dagger" % "2.13"
libraryDependencies += "com.google.dagger" % "dagger-compiler" % "2.13"

javacOptions in Compile := { (managedSourceDirectories in Compile).value.head.mkdirs(); javacOptions.value }

// move the java annotation code into generated directory
javacOptions in Compile ++= Seq("-s", (managedSourceDirectories in Compile).value.head.getAbsolutePath)
