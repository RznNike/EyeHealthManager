plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    namespace = "ru.rznnike.eyehealthmanager.device"

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
}

dependencies {
    implementation(project(":data"))
    implementation(project(":domain"))

    // Desugaring
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // Firebase
    api("com.google.firebase:firebase-core:21.1.1")
    api("com.google.firebase:firebase-messaging:23.4.0")

    // Koin
    api("io.insert-koin:koin-android:" + rootProject.extra["koinVersion"])

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:" + rootProject.extra["coroutinesVersion"])
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:" + rootProject.extra["coroutinesVersion"])
}
