plugins {
    `android-library`
    `kotlin-android`
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