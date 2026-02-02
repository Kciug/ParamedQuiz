import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependencyBundle
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider

internal val Project.libs
    get() = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")

internal fun VersionCatalog.findLibraryOrThrow(name: String): Provider<MinimalExternalModuleDependency> {
    return findLibrary(name).orElseThrow {
        NoSuchElementException("Library $name not found in version catalog")
    }
}

internal fun VersionCatalog.findBundleOrThrow(name: String): Provider<ExternalModuleDependencyBundle> {
    return findBundle(name).orElseThrow {
        NoSuchElementException("Bundle $name not found in version catalog")
    }
}
