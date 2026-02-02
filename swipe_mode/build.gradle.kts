plugins {
    id("paramedquiz.android.feature")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.rafalskrzypczyk.swipe_mode"
}

dependencies {
    implementation(project(":firestore"))
    implementation(project(":auth"))
    implementation(project(":score"))

    implementation(libs.androidx.compose.runtime)
    implementation(libs.kotlinx.serialization.json)

        testImplementation(libs.bundles.unit.test)

    }

    