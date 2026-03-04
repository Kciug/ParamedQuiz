plugins {
    id("paramedquiz.android.feature")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.rafalskrzypczyk.cem_mode"
}

dependencies {
    implementation(project(":firestore"))
    implementation(project(":auth"))
    implementation(project(":score"))
    implementation(project(":billing"))
    implementation(project(":main_mode"))
    
    implementation(libs.billing.ktx)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.hilt.navigation.compose)

    testImplementation(libs.bundles.unit.test)
}
