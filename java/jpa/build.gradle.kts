import com.github.gradle.node.npm.task.NpxTask
import play.gradle.plugin.PlayPlugin

plugins {
    alias(libs.plugins.twirl)
    alias(libs.plugins.play)
    alias(libs.plugins.node)
}

val scalaVersion = System.getProperty("scala.version", PlayPlugin.DEFAULT_SCALA_VERSION).trimEnd { !it.isDigit() }
val server = System.getProperty("backend.server", "netty").let { if (it == "pekko") "pekko-http" else it }

dependencies {
    implementation(platform("org.playframework:play-bom_$scalaVersion:${libs.versions.play.asProvider().get()}"))

    implementation("org.playframework:play-$server-server_$scalaVersion")
    implementation("org.playframework:play-guice_$scalaVersion")
    implementation("org.playframework:play-java-forms_$scalaVersion")
    implementation("org.playframework:play-java-jpa_$scalaVersion")
    implementation("org.playframework:play-filters-helpers_$scalaVersion")
    implementation("org.playframework:play-logback_$scalaVersion")
    implementation(libs.h2)
    implementation(libs.hibernate.core)

    testImplementation(libs.junit)
    testImplementation("org.playframework:play-test_$scalaVersion")
    testImplementation("org.playframework:play-ahc-ws_$scalaVersion:${libs.versions.play.ws.get()}")
    testImplementation(libs.awaitility)
    testImplementation(libs.assertj)
    testImplementation(libs.mockito)
}

val compileCoffeeScript = tasks.register<NpxTask>("compileCoffeeScript") {
    dependsOn(tasks.npmInstall)
    val destDir = sourceSets.main.get().assets.destinationDirectory
    command = "coffee"
    args.addAll("-c", "-m", "-o", destDir.get().toString(), "app/assets/")
    inputs.files("app/assets/javascripts/")
    outputs.dir(destDir)
}

tasks.processAssets {
    dependsOn(compileCoffeeScript)
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
