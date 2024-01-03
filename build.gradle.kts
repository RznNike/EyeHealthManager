buildscript {
    extra.apply {
        set("VERSION_CODE", 15)
        set("VERSION_NAME", "2.1.1.${extra["VERSION_CODE"]}")
        set("APK_NAME", "Eye Health Manager")
        set("MIN_SDK", 24)
        set("TARGET_SDK", 34)

        set("kotlinVersion", "1.9.22")
        set("coroutinesVersion", "1.7.3")
        set("objectboxVersion", "3.7.1")
        set("koinVersion", "3.5.3")
    }

    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.2.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${rootProject.extra["kotlinVersion"]}")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.9")
        classpath("com.google.gms:google-services:4.4.0")
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
