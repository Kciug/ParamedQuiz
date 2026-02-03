plugins {
    id("paramedquiz.android.library")
    id("paramedquiz.android.compose")
    id("paramedquiz.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.rafalskrzypczyk.core"
}

dependencies {
    implementation(libs.androidx.compose.runtime)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.bundles.unit.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
    