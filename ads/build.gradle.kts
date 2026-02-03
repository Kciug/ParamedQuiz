plugins {
    id("paramedquiz.android.library")
    id("paramedquiz.android.hilt")
}

android {
    namespace = "com.rafalskrzypczyk.ads"
}

dependencies {
    implementation(project(":core"))
    implementation(libs.play.services.ads)
}