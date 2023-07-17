plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

dependencies {
    testImplementation(gradleTestKit())
    testImplementation(libs.bundles.kotlin.testing)
}
