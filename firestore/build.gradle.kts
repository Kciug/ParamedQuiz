plugins {
    id("paramedquiz.android.feature")
}

android {
    namespace = "com.rafalskrzypczyk.firestore"

    defaultConfig {
        consumerProguardFiles("proguard-rules.pro")
    }
}

dependencies {
    implementation(libs.androidx.compose.runtime)
    
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.config)

        testImplementation(libs.bundles.unit.test)

    }

    