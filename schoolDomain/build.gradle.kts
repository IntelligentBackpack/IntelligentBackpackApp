plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

dependencies {
    implementation(project(":accessDomain"))
    testImplementation(gradleTestKit())
    testImplementation(libs.bundles.kotlin.testing)
}
