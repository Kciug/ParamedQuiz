import java.util.Properties

plugins {
    id("paramedquiz.android.library")
    id("paramedquiz.android.hilt")
}

android {
    namespace = "com.rafalskrzypczyk.ads"

    val localProperties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localProperties.load(localPropertiesFile.inputStream())
    }

    val admobAppId = localProperties.getProperty("ADMOB_APP_ID") ?: "\"\""
    val interstitialUnitId = localProperties.getProperty("ADMOB_INTERSTITIAL_UNIT_ID") ?: "\"\""

    defaultConfig {
        manifestPlaceholders["admobAppId"] = admobAppId
        buildConfigField("String", "ADMOB_INTERSTITIAL_UNIT_ID", "\"$interstitialUnitId\"")
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core"))
    implementation(libs.play.services.ads)
}