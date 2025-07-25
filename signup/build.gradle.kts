plugins {
    `android-library`
    `kotlin-android`
    kotlin("plugin.serialization") version "2.0.21"
}

apply<SharedGradleProjectConfig>()

android {
    namespace = "com.rafalskrzypczyk.signup"
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":auth"))
    implementation(project(":score"))

    coreKtx()
    implementation(Dependencies.COMPOSE_RUNTIME)
    ui()
    daggerHilt()
    tests()
    kotlinxSerialization()
}