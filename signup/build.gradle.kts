plugins {
    id("paramedquiz.android.feature")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.rafalskrzypczyk.signup"
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(project(":auth"))

    implementation(libs.androidx.compose.runtime)
    implementation(libs.kotlinx.serialization.json)

        testImplementation(libs.bundles.unit.test)

    }

    