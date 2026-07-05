plugins {
    id("paramedquiz.android.feature")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.rafalskrzypczyk.revisions"
}

dependencies {
    implementation(project(":firestore"))
    implementation(project(":score"))
    implementation(project(":main_mode"))
    implementation(project(":cem_mode"))
    implementation(project(":translation_mode"))

    implementation(libs.androidx.compose.runtime)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.bundles.unit.test)
}
