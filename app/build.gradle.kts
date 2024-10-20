import org.gradle.internal.extensions.stdlib.capitalized
import java.util.Properties

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "ru.rznnike.eyehealthmanager"

    compileSdk = rootProject.extra["TARGET_SDK"] as Int
    buildToolsVersion = "35.0.0"

    signingConfigs {
        create("config") {
            storeFile = file("../eyehealthmanager.jks")
            keyAlias = "eyehealthmanager"
            val localProperties = Properties().apply {
                rootProject.file("local.properties").reader().use(::load)
            }
            val keyPass = localProperties.getProperty("PROJECT_KEY_PASSWORD")
            val storePass = localProperties.getProperty("PROJECT_KEYSTORE_PASSWORD")
            if (keyPass.isNullOrBlank() || storePass.isNullOrBlank()) {
                throw GradleException("Not found signing config password properties")
            } else {
                keyPassword = keyPass
                storePassword = storePass
            }
        }
    }

    defaultConfig {
        applicationId = "ru.rznnike.eyehealthmanager"
        minSdk = rootProject.extra["MIN_SDK"] as Int
        targetSdk = rootProject.extra["TARGET_SDK"] as Int
        versionCode = rootProject.extra["VERSION_CODE"] as Int
        versionName = rootProject.extra["VERSION_NAME"] as String
        resourceConfigurations += setOf("en", "ru")
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("config")
            manifestPlaceholders["enableCrashReporting"] = "false"
            versionNameSuffix = " debug"
        }
        register("staging") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("config")
            manifestPlaceholders["enableCrashReporting"] = "true"
            versionNameSuffix = " staging"
        }
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("config")
            manifestPlaceholders["enableCrashReporting"] = "true"
            versionNameSuffix = ""
        }
    }

    lint {
        abortOnError = true
        checkAllWarnings = true
        ignoreWarnings = false
        warningsAsErrors = false
        checkDependencies = true
        htmlReport = true
        explainIssues = true
        noLines = false
        textOutput = file("stdout")
        disable.add("MissingClass")
        disable.add("NewApi")
    }

    applicationVariants.all {
        outputs
            .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
            .forEach {
                it.outputFileName = "${rootProject.extra["APK_NAME"]} ${versionName}.apk"
            }
    }
    afterEvaluate {
        applicationVariants.configureEach {
            val variantName = name.capitalized()
            if (variantName != "Debug") {
                project.tasks["compile${variantName}Sources"].dependsOn(project.tasks["lint${variantName}"])
            }
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

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    bundle {
        abi.enableSplit = false
        language.enableSplit = false
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
    implementation(project(":data"))
    implementation(project(":resources"))

    // Desugaring
    // https://mvnrepository.com/artifact/com.android.tools/desugar_jdk_libs
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:" + rootProject.extra["desugaringVersion"])

    // AndroidX
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    val lifecycleVersion = "2.8.6"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-process:$lifecycleVersion")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    implementation("androidx.annotation:annotation:1.9.0")
    implementation("androidx.fragment:fragment-ktx:1.8.4")
    implementation("androidx.window:window:1.3.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:" + rootProject.extra["coroutinesVersion"])
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:" + rootProject.extra["coroutinesVersion"])
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:" + rootProject.extra["coroutinesVersion"])

    // Material
    implementation("com.google.android.material:material:1.12.0")

    // Firebase
    implementation("com.google.firebase:firebase-crashlytics-ktx:19.2.0")
    implementation("com.google.firebase:firebase-messaging:24.0.2")

    // Koin
    // https://github.com/InsertKoinIO/koin
    implementation("io.insert-koin:koin-core:" + rootProject.extra["koinVersion"])
    implementation("io.insert-koin:koin-android:" + rootProject.extra["koinVersion"])
    testImplementation("io.insert-koin:koin-test:" + rootProject.extra["koinVersion"])
    testImplementation("io.insert-koin:koin-test-junit5:" + rootProject.extra["koinVersion"])

    // Moxy MVP
    // https://github.com/moxy-community/Moxy
    val moxyVersion = "2.2.2"
    implementation("com.github.moxy-community:moxy:$moxyVersion")
    kapt("com.github.moxy-community:moxy-compiler:$moxyVersion")
    implementation("com.github.moxy-community:moxy-androidx:$moxyVersion")
    implementation("com.github.moxy-community:moxy-ktx:$moxyVersion")

    // Cicerone navigation
    // https://github.com/terrakok/Cicerone
    implementation("com.github.terrakok:cicerone:7.1")

    // FastAdapter
    // https://github.com/mikepenz/FastAdapter
    val fastAdapterVersion = "5.7.0"
    implementation("com.mikepenz:fastadapter:$fastAdapterVersion")
    implementation("com.mikepenz:fastadapter-extensions-diff:$fastAdapterVersion")
    implementation("com.mikepenz:fastadapter-extensions-ui:$fastAdapterVersion")
    implementation("com.mikepenz:fastadapter-extensions-binding:$fastAdapterVersion")

    // Image loader
    // https://github.com/coil-kt/coil
    implementation("io.coil-kt:coil:2.7.0")

    // MPAndroidChart
    // https://github.com/PhilJay/MPAndroidChart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Reflection
    implementation("org.jetbrains.kotlin:kotlin-reflect:" + rootProject.extra["kotlinVersion"])

    // ViewBinding
    // https://github.com/kirich1409/ViewBindingPropertyDelegate
    implementation("com.github.kirich1409:viewbindingpropertydelegate-noreflection:1.5.9")

    // FlexboxLayoutManager
    // https://github.com/google/flexbox-layout
    implementation("com.google.android.flexbox:flexbox:3.0.0")

    // Testing
    // https://github.com/junit-team/junit5/
    testImplementation("org.junit.jupiter:junit-jupiter:" + rootProject.extra["junitVersion"])

    // Mocks for testing
    // https://github.com/mockito/mockito
    val mockitoVersion = "5.14.2"
    testImplementation("org.mockito:mockito-core:$mockitoVersion")
    testImplementation("org.mockito:mockito-junit-jupiter:$mockitoVersion")
    // https://github.com/mockito/mockito-kotlin
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
}
