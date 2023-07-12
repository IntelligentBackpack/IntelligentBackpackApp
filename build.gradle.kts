// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.application) apply false
    alias(libs.plugins.library) apply false
    alias(libs.plugins.android) apply false
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.qa)
    alias(libs.plugins.taskTree)
    alias(libs.plugins.sonarqube)
    jacoco
    alias(libs.plugins.dokka)
}

subprojects {
    apply(plugin = "org.danilopianini.gradle-kotlin-qa")
    apply(plugin = "jacoco")
    apply(plugin = "org.jetbrains.dokka")
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}
tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
    }
}

tasks.dokkaHtmlMultiModule {
    outputDirectory.set(buildDir.resolve("docs/partial"))
}

buildscript {
    repositories {
        google()
        jcenter()
        mavenCentral()
    }
    dependencies {
        classpath(libs.build.gradle)
        classpath(libs.gradle.plugin)
        classpath(libs.google.services)
        classpath(libs.firebase.crashlytics.gradle)
        classpath(libs.org.jacoco.core)
    }
}
