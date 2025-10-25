plugins {
    `android-library`
    `kotlin-android`
}

apply<SharedGradleProjectConfig>()

android {
    namespace = "com.rafalskrzypczyk.auth"

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":firestore"))
    implementation(project(":score"))

    coreKtx()
    implementation(Dependencies.COMPOSE_RUNTIME)
    firebase()
    googleCredentials()
    daggerHilt()
    tests()
}