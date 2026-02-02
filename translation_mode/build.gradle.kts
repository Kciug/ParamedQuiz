plugins {
    id("paramedquiz.android.feature")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.rafalskrzypczyk.translation_mode"
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(project(":firestore"))
    implementation(project(":score"))

    implementation(libs.androidx.compose.runtime)
    implementation(libs.kotlinx.serialization.json)

        testImplementation(libs.bundles.unit.test)

    }

    