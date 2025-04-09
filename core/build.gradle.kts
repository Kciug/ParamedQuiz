plugins {
    `android-library`
    `kotlin-android`
}

apply<SharedGradleProjectConfig>()

android {
    namespace = "com.rafalskrzypczyk.core"
}

dependencies {
    coreKtx()
    ui()
    tests()
    daggerHilt()
    kotlinxSerialization()
}