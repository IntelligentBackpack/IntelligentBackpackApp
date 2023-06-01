plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks {
    withType<Test> {
        useJUnitPlatform()
        testLogging {
            showCauses = true
            showStackTraces = true
            showStandardStreams = true
            events(*org.gradle.api.tasks.testing.logging.TestLogEvent.values())
        }
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            allWarningsAsErrors = true
            freeCompilerArgs += listOf("-opt-in=kotlin.RequiresOptIn", "-Xinline-classes")
        }
    }
}

dependencies {
    implementation(project(":accessDomain"))
    implementation(project(":schoolDomain"))
    implementation(project(":desktopDomain"))
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(gradleTestKit())
    testImplementation(libs.bundles.kotlin.testing)
}
