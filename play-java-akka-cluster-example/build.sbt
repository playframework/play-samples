import play.core.PlayVersion

name := """play-java-akka-cluster-example"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.3"

libraryDependencies += guice

libraryDependencies += "com.typesafe.akka" %% "akka-cluster-typed" % PlayVersion.akkaVersion