import com.github.gradle.node.npm.task.NpxTask
import play.gradle.plugin.PlayPlugin

plugins {
    alias(libs.plugins.twirl)
    alias(libs.plugins.play)
    alias(libs.plugins.node)
}

val scalaVersion = System.getProperty("scala.version", PlayPlugin.DEFAULT_SCALA_VERSION).trimEnd { !it.isDigit() }

dependencies {
    implementation(platform("org.playframework:play-bom_$scalaVersion:${libs.versions.play.asProvider().get()}"))

    implementation("org.playframework:play-pekko-http-server_$scalaVersion")
    implementation("org.playframework:play-guice_$scalaVersion")
    implementation("org.playframework:play-java-forms_$scalaVersion")
    implementation("org.playframework:play-logback_$scalaVersion")
    implementation("org.playframework:play-ws_$scalaVersion")
    implementation("org.webjars:webjars-play_$scalaVersion:${libs.versions.webjars.play.get()}")
    implementation(libs.flot)
    implementation(libs.bootstrap)

    testImplementation(libs.junit)
    testImplementation("org.playframework:play-test_$scalaVersion")
    implementation("org.playframework:play-ahc-ws_$scalaVersion:${libs.versions.play.ws.get()}")
    testImplementation(libs.awaitility)
    testImplementation(libs.assertj)
}

val compileCoffeeScript = tasks.register<NpxTask>("compileCoffeeScript") {
    dependsOn(tasks.npmInstall)
    val destDir = sourceSets.main.get().assets.destinationDirectory
    command = "coffee"
    args.addAll("-c", "-m", "-o", destDir.get().toString(), "app/assets/")
    inputs.files("app/assets/javascripts/")
    outputs.dir(destDir)
}

val compileLess = tasks.register<NpxTask>("compileLess") {
    dependsOn(tasks.npmInstall)
    val destDir = sourceSets.main.get().assets.destinationDirectory.dir("stylesheets")
    command = "lessc"
    args.addAll("-x", "app/assets/stylesheets/main.less", destDir.get().file("main.min.css").toString())
    inputs.files("app/assets/stylesheets/")
    outputs.dir(destDir)
}

tasks.processAssets {
    dependsOn(compileCoffeeScript, compileLess)
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
