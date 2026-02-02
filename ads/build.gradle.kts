plugins {
    id("paramedquiz.android.library")
    id("paramedquiz.android.hilt")
}

android {
    namespace = "com.rafalskrzypczyk.ads"
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(project(":core"))
    implementation(libs.play.services.ads)
}