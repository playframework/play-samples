import com.github.gradle.node.npm.task.NpxTask
import play.gradle.plugin.PlayPlugin

plugins {
    alias(libs.plugins.twirl)
    alias(libs.plugins.play)
    alias(libs.plugins.node)
}

val scalaBinaryVersion = System.getProperty("scala.version", PlayPlugin.DEFAULT_SCALA_VERSION).let {
    if (it.startsWith("3")) "3" else it.split('.').take(2).joinToString(".")
}
val server = System.getProperty("backend.server", "netty").let { if (it == "pekko") "pekko-http" else it }

scala {
    scalaVersion = providers.systemProperty("scala.version")
}

twirl {
    scalaVersion = scalaBinaryVersion
}

dependencies {
    implementation(platform("org.playframework:play-bom_$scalaBinaryVersion:${libs.versions.play.get()}"))

    implementation("org.playframework:play-$server-server_$scalaBinaryVersion")
    implementation("org.playframework:play-guice_$scalaBinaryVersion")
    implementation("org.playframework:play-java-forms_$scalaBinaryVersion")
    implementation("org.playframework:play-java-jpa_$scalaBinaryVersion")
    implementation("org.playframework:play-filters-helpers_$scalaBinaryVersion")
    implementation("org.playframework:play-logback_$scalaBinaryVersion")
    implementation(libs.h2)
    implementation(libs.hibernate.core)

    testImplementation(libs.junit)
    testImplementation("org.playframework:play-test_$scalaBinaryVersion")
    testImplementation("org.playframework:play-ahc-ws_$scalaBinaryVersion:${libs.versions.play.get()}")
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
        url = uri("https://central.sonatype.com/repository/maven-snapshots")
    }
    mavenLocal()
}
