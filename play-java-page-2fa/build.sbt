name := """play-java-page-2fa"""
organization := "com.revol"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.18"

libraryDependencies += guice

libraryDependencies += "dev.samstevens.totp" % "totp" % "1.7.1"
