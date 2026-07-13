plugins {
    id("paramedquiz.android.application")
    id("paramedquiz.android.compose")
    id("paramedquiz.android.hilt")
    alias(libs.plugins.google.services)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.frontfolks.mediquiz"

    defaultConfig {
        androidResources {
            @Suppress("UnstableApiUsage")
            localeFilters += "pl"
        }
    }

    signingConfigs {
        create("release") {
            val keystoreFile = file("keystore.jks")
            if (keystoreFile.exists()) {
                storeFile = keystoreFile
                storePassword = System.getenv("SIGNING_STORE_PASSWORD")
                keyAlias = System.getenv("SIGNING_KEY_ALIAS")
                keyPassword = System.getenv("SIGNING_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
        }
        getByName("staging") {
            // W CI podpisujemy kluczem uploadu (Play wymaga tego klucza dla tej apki);
            // lokalnie (brak keystore.jks) fallback na debug, zeby build sie skladal
            signingConfig = if (file("keystore.jks").exists()) {
                signingConfigs.getByName("release")
            } else {
                signingConfigs.getByName("debug")
            }
        }
    }
}

hilt {
    enableAggregatingTask = false
}

dependencies {
    implementation(project(":core"))
    implementation(project(":home"))
    implementation(project(":signup"))
    implementation(project(":auth"))
    implementation(project(":main_mode"))
    implementation(project(":swipe_mode"))
    implementation(project(":score"))
    implementation(project(":translation_mode"))
    implementation(project(":cem_mode"))
    implementation(project(":ads"))
    implementation(project(":billing"))
    implementation(project(":firestore"))
    implementation(project(":revisions"))
    implementation(project(":notifications"))


    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.compose.runtime)
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Harness E2E (Robolectric + Hilt + Compose UI test) — testy uruchamiane na JVM.
    testImplementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.bundles.robolectric.e2e)
    kaptTest(libs.hilt.compiler)
    debugImplementation(libs.androidx.ui.test.manifest)
    
    implementation(libs.kotlinx.serialization.json)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
}
    
    