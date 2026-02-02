plugins {
    id("paramedquiz.android.feature")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.rafalskrzypczyk.main_mode"
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(project(":firestore"))
    implementation(project(":auth"))
    implementation(project(":score"))
    implementation(project(":billing"))
    
    implementation(libs.billing.ktx)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.kotlinx.serialization.json)

        testImplementation(libs.bundles.unit.test)

    }

    