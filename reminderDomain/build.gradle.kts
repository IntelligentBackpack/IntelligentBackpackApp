plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

dependencies {
    implementation(project(":accessDomain"))
    implementation(project(":schoolDomain"))
    implementation(project(":desktopDomain"))
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(gradleTestKit())
    testImplementation(libs.bundles.kotlin.testing)
}
