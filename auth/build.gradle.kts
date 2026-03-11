plugins {
    id("paramedquiz.android.feature")
    alias(libs.plugins.secrets)
}

android {
    namespace = "com.rafalskrzypczyk.auth"

    buildFeatures {
        buildConfig = true
    }
}

secrets {
    propertiesFileName = "local.properties"
    defaultPropertiesFileName = "local.defaults.properties"
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
    
    