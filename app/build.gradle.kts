plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    //id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.plugin.compose")

    id("kotlin-kapt")
}

android {
    namespace = "com.rafalskrzypczyk.paramedquiz"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.rafalskrzypczyk.paramedquiz"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    coreKtx()
    ui()
    tests()
    daggerHilt()
}