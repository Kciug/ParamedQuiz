import com.android.build.gradle.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project

class SharedGradleProjectConfig : Plugin<Project> {
    override fun apply(target: Project) {
        applyPlugins(target)
        setProjectConfig(target)
    }

    private fun applyPlugins(project: Project) {
        project.apply {
            plugin("android-library")
            plugin("kotlin-android")
            plugin("org.jetbrains.kotlin.plugin.compose")
            plugin("com.google.dagger.hilt.android")
            plugin("kotlin-kapt")
        }
    }

    private fun setProjectConfig(project: Project) {
        project.android().apply {
            compileSdk = ProjectConfig.COMPILE_SDK

            defaultConfig{
                minSdk = ProjectConfig.MIN_SDK
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            }

            testOptions {
                targetSdk = ProjectConfig.TARGET_SDK
            }

            lint {
                targetSdk = ProjectConfig.TARGET_SDK
            }

            compileOptions{
                sourceCompatibility = JavaVersion.VERSION_11
                targetCompatibility = JavaVersion.VERSION_11
            }

            buildFeatures{
                compose = true
            }
        }
    }

    private fun Project.android(): LibraryExtension {
        return extensions.getByType(LibraryExtension::class.java)
    }
}