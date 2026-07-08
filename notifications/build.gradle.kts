plugins {
    id("paramedquiz.android.feature")
}

android {
    namespace = "com.rafalskrzypczyk.notifications"
}

dependencies {
    testImplementation(libs.bundles.unit.test)
}
