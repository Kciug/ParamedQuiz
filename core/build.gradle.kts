plugins {
    id("paramedquiz.android.library")
    id("paramedquiz.android.compose")
    id("paramedquiz.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.rafalskrzypczyk.core"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        // Domyslnie wylaczone (release dziedziczy false); wlaczane w debug i staging ponizej
        buildConfigField("boolean", "DEV_OPTIONS_ENABLED", "false")
    }

    buildTypes {
        getByName("debug") {
            buildConfigField("boolean", "DEV_OPTIONS_ENABLED", "true")
        }
        getByName("staging") {
            buildConfigField("boolean", "DEV_OPTIONS_ENABLED", "true")
        }
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.google.play.review)

    testImplementation(libs.bundles.unit.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
    