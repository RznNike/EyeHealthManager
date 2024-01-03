plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

android {
    namespace = "ru.rznnike.eyehealthmanager.domain"

    compileSdk = rootProject.extra["TARGET_SDK"] as Int

    defaultConfig {
        minSdk = rootProject.extra["MIN_SDK"] as Int
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
        register("staging") {
            isMinifyEnabled = true
            consumerProguardFiles("proguard-rules.pro")
        }
        release {
            isMinifyEnabled = true
            consumerProguardFiles("proguard-rules.pro")
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
    @Suppress("UnstableApiUsage")
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}

dependencies {
    implementation(project(":resources"))

    // Desugaring
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // Android
    api("androidx.core:core-ktx:1.12.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:" + rootProject.extra["coroutinesVersion"])
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:" + rootProject.extra["coroutinesVersion"])

    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
}