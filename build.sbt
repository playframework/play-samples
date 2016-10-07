name := """play-java-intro"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.8"

libraryDependencies += javaJpa

// https://mvnrepository.com/artifact/dom4j/dom4j
libraryDependencies += "dom4j" % "dom4j" % "1.6"

libraryDependencies += "org.mockito" % "mockito-core" % "2.1.0"

libraryDependencies += javaWs % "test"

// https://mvnrepository.com/artifact/org.hibernate/hibernate-core
// must exclude dom4j in hibernate core because it causes staxeventreader exceptions
// http://stackoverflow.com/questions/36222306/caused-by-java-lang-classnotfoundexception-org-dom4j-io-staxeventreader
libraryDependencies += "org.hibernate" % "hibernate-core" % "5.2.3.Final" exclude("dom4j", "dom4j") exclude("javax.transaction", "jta") exclude("org.slf4j", "slf4j-api")
