buildscript {
    extra.apply {
        set("VERSION_CODE", 18)
        set("VERSION_NAME", "2.3.0.${extra["VERSION_CODE"]}")
        set("APK_NAME", "Eye Health Manager")
        set("MIN_SDK", 24)
        set("TARGET_SDK", 35)

        set("kotlinVersion", "2.1.20")
        set("coroutinesVersion", "1.10.1")
        set("objectboxVersion", "4.2.0")
        set("koinVersion", "4.0.3")
        set("junitVersion", "5.12.1")
        set("desugaringVersion", "2.1.5")
    }

    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.9.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${rootProject.extra["kotlinVersion"]}")
        classpath("com.google.firebase:firebase-crashlytics-gradle:3.0.3")
        classpath("com.google.gms:google-services:4.4.2")
        classpath("io.objectbox:objectbox-gradle-plugin:${rootProject.extra["objectboxVersion"]}")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}
