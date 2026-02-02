plugins {
    `android-library`
    `kotlin-android`
    kotlin("plugin.serialization") version "2.0.21"
}

apply<SharedGradleProjectConfig>()

android {
    namespace = "com.rafalskrzypczyk.main_mode"
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":firestore"))
    implementation(project(":auth"))
    implementation(project(":score"))
    implementation(project(":billing"))
    implementation(libs.billing.ktx)

    coreKtx()
    implementation(Dependencies.COMPOSE_RUNTIME)
    ui()
    daggerHilt()
    tests()
    kotlinxSerialization()
}