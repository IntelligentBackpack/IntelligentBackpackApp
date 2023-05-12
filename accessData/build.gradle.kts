import com.google.protobuf.gradle.id
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.config.KotlinCompilerVersion.VERSION as KOTLIN_VERSION

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.protobuf.plugin)
}

android {
    namespace = "com.intelligentbackpack.accessdata"
    compileSdk = 33

    defaultConfig {
        minSdk = 29

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        testInstrumentationRunnerArguments["clearPackageData"] = "true"
    }

    testOptions {
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
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
        }
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.androidx.security.crypto)
    implementation(libs.bundles.retrofit)
    implementation(libs.bundles.protobuf)
    implementation(libs.bundles.grpc)
    implementation(project(":networkUtility"))
    implementation(project(":accessDomain"))
    testImplementation(gradleTestKit())
    testImplementation(libs.bundles.kotlin.testing)
    androidTestImplementation(libs.bundles.androidTest)
    androidTestImplementation(libs.mockito.android)
    androidTestUtil(libs.orchestrator)
}

configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.jetbrains.kotlin" && requested.name.startsWith("kotlin")) {
            useVersion(KOTLIN_VERSION)
            because("All Kotlin modules should use the same version, and compiler uses $KOTLIN_VERSION")
        }
    }
}

tasks {
    withType<Test> {
        useJUnitPlatform()
        testLogging {
            showCauses = true
            showStackTraces = true
            showStandardStreams = true
            events(*TestLogEvent.values())
        }
    }
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
            allWarningsAsErrors = true
            freeCompilerArgs += listOf("-opt-in=kotlin.RequiresOptIn", "-Xinline-classes")
        }
    }
}

protobuf {
    protoc {
        // The artifact spec for the Protobuf Compiler
        artifact = "com.google.protobuf:protoc:${libs.versions.protobuf.compiler.get()}"
    }
    plugins {
        // Optional: an artifact spec for a protoc plugin, with "grpc" as
        // the identifier, which can be referred to in the "plugins"
        // container of the "generateProtoTasks" closure.
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${libs.versions.grpc.protobuf.get()}"
        }
    }

    generateProtoTasks {
        all().forEach() {
            // The "plugins" container is a NamedDomainObjectContainer<GenerateProtoTask.PluginOptions>
            // which can be used to configure the plugins applied to the task.
            it.builtins {
                id("kotlin") {
                }
                id("java") {
                }
            }
        }
    }
}
