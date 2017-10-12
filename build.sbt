name := """play-java-dagger2-example"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.3"

libraryDependencies += ws

libraryDependencies += "com.google.dagger" % "dagger" % "2.12"
libraryDependencies += "com.google.dagger" % "dagger-compiler" % "2.12"

javacOptions in Compile := { (managedSourceDirectories in Compile).value.head.mkdirs(); javacOptions.value }

// move the java annotation code into generated directory
javacOptions in Compile ++= Seq("-s", (managedSourceDirectories in Compile).value.head.getAbsolutePath)
