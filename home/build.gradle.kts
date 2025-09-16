plugins {
    `android-library`
    `kotlin-android`
}

apply<SharedGradleProjectConfig>()

android {
    namespace = "com.rafalskrzypczyk.home"

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":auth"))
    implementation(project(":score"))
    implementation(project(":main_mode"))
    implementation(project(":swipe_mode"))

    coreKtx()
    implementation(Dependencies.COMPOSE_RUNTIME)
    ui()
    firebase()
    daggerHilt()
    tests()
}