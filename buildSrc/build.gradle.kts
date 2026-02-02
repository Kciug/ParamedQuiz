plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation(files("../gradle/libs.versions.toml"))
    implementation("com.android.tools.build:gradle:8.13.2")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.0")
    implementation("org.jetbrains.kotlin.plugin.compose:org.jetbrains.kotlin.plugin.compose.gradle.plugin:2.3.0")
    implementation("com.google.dagger:hilt-android-gradle-plugin:2.55")
}


gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "paramedquiz.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "paramedquiz.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidCompose") {
            id = "paramedquiz.android.compose"
            implementationClass = "AndroidComposeConventionPlugin"
        }
                register("androidHilt") {
                    id = "paramedquiz.android.hilt"
                    implementationClass = "AndroidHiltConventionPlugin"
                }
                register("androidFeature") {
                    id = "paramedquiz.android.feature"
                    implementationClass = "AndroidFeatureConventionPlugin"
                }
            }
        }
        