plugins {
    id("paramedquiz.android.application")
    id("paramedquiz.android.compose")
    id("paramedquiz.android.hilt")
    alias(libs.plugins.google.services)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.rafalskrzypczyk.paramedquiz"

    defaultConfig {
        resourceConfigurations += "pl"
    }

    signingConfigs {
        create("release") {
            val keystoreFile = file("keystore.jks")
            if (keystoreFile.exists()) {
                storeFile = keystoreFile
                storePassword = System.getenv("SIGNING_STORE_PASSWORD")
                keyAlias = System.getenv("SIGNING_KEY_ALIAS")
                keyPassword = System.getenv("SIGNING_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
        }
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

hilt {
    enableAggregatingTask = true
}

dependencies {
    implementation(project(":core"))
    implementation(project(":home"))
    implementation(project(":signup"))
    implementation(project(":auth"))
    implementation(project(":main_mode"))
    implementation(project(":swipe_mode"))
    implementation(project(":score"))
    implementation(project(":translation_mode"))
    implementation(project(":ads"))
    implementation(project(":billing"))
    implementation(project(":firestore"))

    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.compose.runtime)
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    
    implementation(libs.kotlinx.serialization.json)
}