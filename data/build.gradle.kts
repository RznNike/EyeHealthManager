plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

android {
    namespace = "ru.rznnike.eyehealthmanager.data"

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
    val stagingApi by configurations

    implementation(project(":domain"))
    implementation(project(":resources"))

    // Desugaring
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // Shared preferences
    // https://github.com/tfcporciuncula/flow-preferences
    api("com.fredporciuncula:flow-preferences:1.9.1")

    // ObjectBox
    // https://github.com/objectbox/objectbox-java
    debugApi("io.objectbox:objectbox-android-objectbrowser:" + rootProject.extra["objectboxVersion"])
    stagingApi("io.objectbox:objectbox-android:" + rootProject.extra["objectboxVersion"])
    releaseApi("io.objectbox:objectbox-android:" + rootProject.extra["objectboxVersion"])
}

apply(plugin = "io.objectbox")