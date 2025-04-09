plugins {
    `android-library`
    `kotlin-android`
}

apply<SharedGradleProjectConfig>()

android {
    namespace = "com.rafalskrzypczyk.firestore"
}

dependencies {
    implementation(project(":core"))

    coreKtx()
    firebase()
    daggerHilt()
    tests()
}