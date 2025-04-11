import play.TemplateImports
import play.gradle.plugin.PlayPlugin

plugins {
    alias(libs.plugins.twirl)
    alias(libs.plugins.play)
}

val scalaVersion = System.getProperty("scala.version", PlayPlugin.DEFAULT_SCALA_VERSION).trimEnd { !it.isDigit() }
val server = System.getProperty("backend.server", "netty").let { if (it == "pekko") "pekko-http" else it }

dependencies {
    implementation(platform("org.playframework:play-bom_$scalaVersion:${libs.versions.play.get()}"))

    implementation("org.playframework:play-$server-server_$scalaVersion")
    implementation("org.playframework:play-java-forms_$scalaVersion")
    implementation("org.playframework:play-ahc-ws_$scalaVersion")
    implementation("org.playframework:play-filters-helpers_$scalaVersion")
    implementation("org.playframework:play-logback_$scalaVersion")
    implementation(libs.dagger)
    annotationProcessor(libs.dagger.compiler)

    testImplementation(libs.junit)
    testImplementation("org.playframework:play-test_$scalaVersion")
}

sourceSets {
    main {
        twirl {
            templateImports.set(TemplateImports.defaultJavaTemplateImports)
        }
    }
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
