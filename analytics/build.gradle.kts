plugins {
    id("paramedquiz.android.library")
    id("paramedquiz.android.hilt")
}

android {
    namespace = "com.rafalskrzypczyk.analytics"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        // Wariant `staging` ma ten sam applicationId co release, wiec bez tego pola ruch testowy
        // jest nieodroznialny od produkcyjnego (zarowno w GA4, jak i w Crashlytics).
        buildConfigField("String", "BUILD_TYPE_NAME", "\"release\"")
    }

    buildTypes {
        getByName("debug") {
            buildConfigField("String", "BUILD_TYPE_NAME", "\"debug\"")
        }
        getByName("staging") {
            buildConfigField("String", "BUILD_TYPE_NAME", "\"staging\"")
        }
    }
}

dependencies {
    implementation(project(":core"))

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    testImplementation(libs.bundles.unit.test)
}
