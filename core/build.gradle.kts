plugins {
    `android-library`
    `kotlin-android`
    kotlin("plugin.serialization") version "2.0.21"
}

apply<SharedGradleProjectConfig>()

android {
    namespace = "com.rafalskrzypczyk.core"

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    coreKtx()
    implementation(Dependencies.COMPOSE_RUNTIME)
    ui()
    tests()
    daggerHilt()
    kotlinxSerialization()
}