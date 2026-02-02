import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            val extension = extensions.findByName("android") as? CommonExtension<*, *, *, *, *, *>
            extension?.apply {
                buildFeatures {
                    compose = true
                }
            }

            dependencies {
                val bom = libs.findLibraryOrThrow("androidx-compose-bom")
                add("implementation", platform(bom))
                add("androidTestImplementation", platform(bom))
                add("implementation", libs.findBundleOrThrow("compose"))
                add("debugImplementation", libs.findLibraryOrThrow("androidx-ui-tooling"))
            }
        }
    }
}