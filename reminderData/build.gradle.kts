import com.google.protobuf.gradle.id

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    alias(libs.plugins.protobuf.plugin)
}

android {
    namespace = "com.intelligentbackpack.reminderdata"
    compileSdk = 33

    defaultConfig {
        minSdk = 29

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        testInstrumentationRunnerArguments["clearPackageData"] = "true"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true",
                )
            }
        }
    }

    testOptions {
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    packaging {
        resources {
            merges += "META-INF/INDEX.LIST"
            merges += "META-INF/LICENSE.md"
            merges += "META-INF/LICENSE-notice.md"
        }
    }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.bundles.retrofit)
    implementation(libs.bundles.protobuf)
    implementation(libs.bundles.grpc)
    implementation(libs.bundles.room)
    kapt(libs.androidx.room.compiler)
    implementation(project(":networkUtility"))
    implementation(project(":reminderDomain"))
    implementation(project(":accessDomain"))
    implementation(project(":schoolData"))
    testImplementation(gradleTestKit())
    testImplementation(libs.bundles.kotlin.testing)
    androidTestImplementation(libs.bundles.androidTest)
    androidTestImplementation(libs.mockk.android)
    androidTestUtil(libs.orchestrator)
}

tasks.dokkaHtmlPartial {
    dependsOn(tasks.findByName("kaptDebugKotlin"))
    dependsOn(tasks.findByName("kaptReleaseKotlin"))
}
