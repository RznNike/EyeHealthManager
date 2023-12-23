plugins {
    id("com.android.library")
}

android {
    namespace = "ru.rznnike.eyehealthmanager.resources"

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
}