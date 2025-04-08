import play.gradle.plugin.PlayPlugin

plugins {
    alias(libs.plugins.twirl)
    alias(libs.plugins.play)
}

val scalaVersion = System.getProperty("scala.version", PlayPlugin.DEFAULT_SCALA_VERSION).trimEnd { !it.isDigit() }

dependencies {
    implementation(platform("com.typesafe.play:play-bom_$scalaVersion:${libs.versions.play.get()}"))

    implementation("com.typesafe.play:play-akka-http-server_$scalaVersion")
    implementation("com.typesafe.play:play-guice_$scalaVersion")
    implementation("com.typesafe.play:play-java-forms_$scalaVersion")
    implementation("com.typesafe.play:play-logback_$scalaVersion")

    testImplementation(libs.junit)
    testImplementation("com.typesafe.play:play-test_$scalaVersion")
}

tasks.withType<ScalaCompile>().configureEach {
    options.compilerArgs.addAll(listOf("-Xlint:unchecked", "-Xlint:deprecation", "-Werror"))
}

// TODO: Remove after release Play Gradle Plugin
repositories {
    mavenCentral()
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
    mavenLocal()
}
