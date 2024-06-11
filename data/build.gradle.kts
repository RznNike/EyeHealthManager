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
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            consumerProguardFiles("proguard-rules.pro")
        }
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
    implementation(project(":domain"))

    val stagingApi by configurations

    // Desugaring
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:" + rootProject.extra["desugaringVersion"])

    // AndroidX
    implementation("androidx.documentfile:documentfile:1.0.1")

    // Shared preferences
    // https://github.com/tfcporciuncula/flow-preferences
    api("com.fredporciuncula:flow-preferences:1.9.1")

    // ObjectBox
    // https://github.com/objectbox/objectbox-java
    debugApi("io.objectbox:objectbox-android-objectbrowser:" + rootProject.extra["objectboxVersion"])
    stagingApi("io.objectbox:objectbox-android:" + rootProject.extra["objectboxVersion"])
    releaseApi("io.objectbox:objectbox-android:" + rootProject.extra["objectboxVersion"])

    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter:" + rootProject.extra["junitVersion"])
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:" + rootProject.extra["coroutinesVersion"])
}

apply(plugin = "io.objectbox")