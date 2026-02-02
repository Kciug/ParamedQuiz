import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.google.dagger.hilt.android")
                apply("org.jetbrains.kotlin.kapt")
            }

            dependencies {
                add("implementation", libs.findLibraryOrThrow("hilt-android"))
                add("kapt", libs.findLibraryOrThrow("hilt-compiler"))
                add("implementation", libs.findLibraryOrThrow("hilt-navigation-compose"))
            }
        }
    }
}
