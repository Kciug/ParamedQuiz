plugins {
    `android-library`
    `kotlin-android`
}

apply<SharedGradleProjectConfig>()

android {
    namespace = "com.rafalskrzypczyk.firestore"
//    kotlinOptions {
//        jvmTarget = "11"
//    }
}

dependencies {
    coreKtx()
    firebase()
    daggerHilt()
    tests()
}