plugins {
    id("paramedquiz.android.feature")
}

android {
    namespace = "com.rafalskrzypczyk.auth"
}

dependencies {
    implementation(project(":firestore"))
    implementation(project(":score"))

    implementation(libs.androidx.compose.runtime)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)

    implementation(libs.androidx.credentials)

    implementation(libs.androidx.credentials.play.services)

    implementation(libs.google.identity.googleid)

    testImplementation(libs.bundles.unit.test)
}
    
    