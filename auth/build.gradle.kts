plugins {
    `android-library`
    `kotlin-android`
}

apply<SharedGradleProjectConfig>()

android {
    namespace = "com.rafalskrzypczyk.auth"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":firestore"))

    coreKtx()
    firebase()
    daggerHilt()
    tests()
}