plugins {
    `android-library`
    `kotlin-android`
}

apply<SharedGradleProjectConfig>()

android {
    namespace = "com.rafalskrzypczyk.firestore"

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(project(":core"))

    coreKtx()
    implementation(Dependencies.COMPOSE_RUNTIME)
    firebase()
    daggerHilt()
    tests()
}