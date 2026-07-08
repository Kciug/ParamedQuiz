plugins {
    id("paramedquiz.android.feature")
}

android {
    namespace = "com.rafalskrzypczyk.notifications"
}

dependencies {
    implementation(project(":score"))

    implementation(libs.androidx.work.runtime.ktx)

    testImplementation(libs.bundles.unit.test)
}
