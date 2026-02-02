plugins {
    id("paramedquiz.android.library")
    id("paramedquiz.android.compose")
    id("paramedquiz.android.hilt")
}

android {
    namespace = "com.rafalskrzypczyk.billing"

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(project(":core"))
    implementation(libs.billing.ktx)
    
        testImplementation(libs.bundles.unit.test)
    
    }
    
    