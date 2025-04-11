import org.gradle.api.artifacts.dsl.DependencyHandler

object Dependencies {
    const val ANDROID_CORE_KTX = "androidx.core:core-ktx:${Versions.CORE_KTX}"

    // UI
    const val COMPOSE_BOM = "androidx.compose:compose-bom:${Versions.COMPOSE_BOM}"
    const val COMPOSE_UI = "androidx.compose.ui:ui"
    const val COMPOSE_UI_GRAPHICS = "androidx.compose.ui:ui-graphics"
    const val COMPOSE_UI_TOOLING = "androidx.compose.ui:ui-tooling"
    const val COMPOSE_UI_TOOLING_PREVIEW = "androidx.compose.ui:ui-tooling-preview"
    const val COMPOSE_MATERIAL3 = "androidx.compose.material3:material3:${Versions.MATERIAL3}"
    const val COMPOSE_NAVIGATION = "androidx.navigation:navigation-compose:${Versions.NAVIGATION_COMPOSE}"
    const val COMPOSE_RUNTIME = "androidx.compose.runtime:runtime"
    const val COMPOSE_VIEWMODEL = "androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.VIEWMODEL_COMPOSE}"
    const val ICONS_EXT = "androidx.compose.material:material-icons-extended-android:${Versions.MATERIAL_ICONS_EXT}"

    // Test
    const val JUNIT = "junit:junit:${Versions.JUNIT}"
    const val ANDROIDX_JUNIT = "androidx.test.ext:junit:${Versions.JUNIT_VERSION}"
    const val ANDROIDX_ESPRESSO_CORE = "androidx.test.espresso:espresso-core:${Versions.ESPRESSO_CORE}"
    const val COROUTINE_TEST = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.COROUTINE_TEST}"
    const val MOCKK = "io.mockk:mockk:${Versions.MOCKK}"
    const val TURBINE = "app.cash.turbine:turbine:${Versions.TURBINE}"

    // HILT
    const val HILT_ANDROID = "com.google.dagger:hilt-android:${Versions.HILT}"
    const val HILT_COMPILER = "com.google.dagger:hilt-android-compiler:${Versions.HILT}"
    const val HILT_AGP = "com.google.dagger:hilt-android-gradle-plugin:${Versions.HILT}"

    //FIREBASE BOM
    const val FIREBASE_BOM = "com.google.firebase:firebase-bom:${Versions.FIREBASE_BOM}"

    //FIREBASE AUTH
    const val FIREBASE_AUTH = "com.google.firebase:firebase-auth-ktx"
    const val FIREBASE_FIRESTORE = "com.google.firebase:firebase-firestore-ktx"

    // Json serializer
    const val KOTLINX_SERIALIZATION_JSON = "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.SERIALIZATION_JSON}"
}

fun DependencyHandler.coreKtx() = implementation(Dependencies.ANDROID_CORE_KTX)

fun DependencyHandler.composeRuntime() = {
    implementation(Dependencies.COMPOSE_RUNTIME)
}

fun DependencyHandler.ui() {
    implementation(Dependencies.COMPOSE_BOM)
    implementation(Dependencies.COMPOSE_UI)
    implementation(Dependencies.COMPOSE_UI_GRAPHICS)
    implementation(Dependencies.COMPOSE_UI_TOOLING)
    implementation(Dependencies.COMPOSE_UI_TOOLING_PREVIEW)
    implementation(Dependencies.COMPOSE_MATERIAL3)
    implementation(Dependencies.COMPOSE_NAVIGATION)
    implementation(Dependencies.COMPOSE_VIEWMODEL)
    implementation(Dependencies.ICONS_EXT)
}

fun DependencyHandler.tests() {
    test(Dependencies.JUNIT)
    androidTest(Dependencies.ANDROIDX_JUNIT)
    androidTest(Dependencies.ANDROIDX_ESPRESSO_CORE)
    testImplementation(Dependencies.COROUTINE_TEST)
    testImplementation(Dependencies.MOCKK)
    testImplementation(Dependencies.TURBINE)
}

fun DependencyHandler.daggerHilt() {
    implementation(Dependencies.HILT_ANDROID)
    kapt(Dependencies.HILT_COMPILER)
}

fun DependencyHandler.firebase() {
    implementation(platform(Dependencies.FIREBASE_BOM))
    implementation(Dependencies.FIREBASE_AUTH)
    implementation(Dependencies.FIREBASE_FIRESTORE)
}

fun DependencyHandler.kotlinxSerialization() {
    implementation(Dependencies.KOTLINX_SERIALIZATION_JSON)
}