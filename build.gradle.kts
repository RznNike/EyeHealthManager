buildscript {
    extra.apply {
        set("VERSION_CODE", 19)
        set("VERSION_NAME", "2.4.0.${extra["VERSION_CODE"]}")
        set("APK_NAME", "Eye Health Manager")
        set("MIN_SDK", 24)
        set("TARGET_SDK", 36)

        set("kotlinVersion", "2.2.10")
        set("coroutinesVersion", "1.10.2")
        set("objectboxVersion", "4.3.1")
        set("koinVersion", "4.1.1")
        set("junitVersion", "5.13.4")
        set("desugaringVersion", "2.1.5")
    }

    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.13.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${rootProject.extra["kotlinVersion"]}")
        classpath("com.google.firebase:firebase-crashlytics-gradle:3.0.6")
        classpath("com.google.gms:google-services:4.4.3")
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
