buildscript {
    extra.apply {
        set("VERSION_CODE", 17)
        set("VERSION_NAME", "2.2.1.${extra["VERSION_CODE"]}")
        set("APK_NAME", "Eye Health Manager")
        set("MIN_SDK", 24)
        set("TARGET_SDK", 34)

        set("kotlinVersion", "1.9.23")
        set("coroutinesVersion", "1.7.3")
        set("objectboxVersion", "4.0.1")
        set("koinVersion", "3.5.6")
        set("junitVersion", "5.10.2")
        set("desugaringVersion", "2.0.4")
    }

    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.5.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${rootProject.extra["kotlinVersion"]}")
        classpath("com.google.firebase:firebase-crashlytics-gradle:3.0.1")
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
