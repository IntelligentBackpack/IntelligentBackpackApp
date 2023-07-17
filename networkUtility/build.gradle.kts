plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

dependencies {
    implementation(libs.json)
    implementation(libs.bundles.retrofit)
    testImplementation(gradleTestKit())
    testImplementation(libs.bundles.kotlin.testing)
}
