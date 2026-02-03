import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("paramedquiz.android.library")
                apply("paramedquiz.android.compose")
                apply("paramedquiz.android.hilt")
            }

            dependencies {
                add("implementation", project(":core"))
            }
        }
    }
}
