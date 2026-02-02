plugins {
    id("paramedquiz.android.feature")
}

android {
    namespace = "com.rafalskrzypczyk.firestore"

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.compose.runtime)
    
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)

        testImplementation(libs.bundles.unit.test)

    }

    