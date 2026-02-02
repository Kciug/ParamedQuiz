plugins {
    id("paramedquiz.android.feature")
}

android {
    namespace = "com.rafalskrzypczyk.home"

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(project(":auth"))
    implementation(project(":firestore"))
    implementation(project(":main_mode"))
    implementation(project(":swipe_mode"))
    implementation(project(":translation_mode"))
    implementation(project(":score"))
    implementation(project(":billing"))
    
    implementation(libs.billing.ktx)
    implementation(libs.androidx.compose.runtime)
    
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)

        testImplementation(libs.bundles.unit.test)

    }

    