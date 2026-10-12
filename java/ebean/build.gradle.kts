import play.TemplateImports
import play.gradle.plugin.PlayPlugin

plugins {
    alias(libs.plugins.ebean)
    alias(libs.plugins.twirl)
    alias(libs.plugins.play)
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
    implementation(platform("org.playframework:play-bom_$scalaBinaryVersion:${libs.versions.play.asProvider().get()}"))

    implementation("org.playframework:play-$server-server_$scalaBinaryVersion")
    implementation("org.playframework:play-guice_$scalaBinaryVersion")
    implementation("org.playframework:play-jdbc_$scalaBinaryVersion")
    implementation("org.playframework:play-java-forms_$scalaBinaryVersion")
    implementation("org.playframework:play-filters-helpers_$scalaBinaryVersion")
    implementation("org.playframework:play-logback_$scalaBinaryVersion")
    implementation("org.playframework:play-ebean_$scalaBinaryVersion:${libs.versions.play.ebean.get()}")
    implementation(libs.h2)
    annotationProcessor("io.ebean:querybean-generator:${libs.versions.ebean.get()}")

    testImplementation(libs.junit)
    testImplementation("org.playframework:play-test_$scalaBinaryVersion")
    testImplementation(libs.assertj)
    testImplementation(libs.awaitility)
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
        url = uri("https://central.sonatype.com/repository/maven-snapshots")
    }
    mavenLocal()
}
