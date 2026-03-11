plugins {
    id("paramedquiz.android.library")
    id("paramedquiz.android.hilt")
    alias(libs.plugins.secrets)
}

android {
    namespace = "com.rafalskrzypczyk.ads"

    buildFeatures {
        buildConfig = true
    }
}

secrets {
    propertiesFileName = "local.properties"
    defaultPropertiesFileName = "local.defaults.properties"
}

dependencies {
    implementation(project(":core"))
    implementation(libs.play.services.ads)
}