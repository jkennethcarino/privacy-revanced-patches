package dev.jkcarino.revanced.patches.all.firebase

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotation.Patch
import dev.jkcarino.revanced.util.createElement
import dev.jkcarino.revanced.util.filterToElements
import dev.jkcarino.revanced.util.firstElementByTagName

@Patch(
    name = "Deactivate Firebase Performance Monitoring",
    description = "Deactivates the collection of performance data on app start up time, network requests, and other related metrics.",
    use = false
)
@Suppress("unused")
object DeactivateFirebasePerfPatch : ResourcePatch() {
    private const val META_DATA_TAG = "meta-data"
    private const val ATTRIBUTE_NAME = "android:name"
    private const val ATTRIBUTE_VALUE = "android:value"
    private const val FIREBASE_PERF_DEACTIVATED = "firebase_performance_collection_deactivated"

    override fun execute(context: ResourceContext) {
        context.xmlEditor["AndroidManifest.xml"].use { editor ->
            val document = editor.file
            val application = document.firstElementByTagName("application")

            val firebasePerfMetaData = application.getElementsByTagName(META_DATA_TAG)
                .filterToElements()
                .firstOrNull { it.getAttribute(ATTRIBUTE_NAME) == FIREBASE_PERF_DEACTIVATED }
                ?.setAttribute(ATTRIBUTE_VALUE, "true")

            if (firebasePerfMetaData == null) {
                application.appendChild(
                    document.createElement(META_DATA_TAG) {
                        setAttribute(ATTRIBUTE_NAME, FIREBASE_PERF_DEACTIVATED)
                        setAttribute(ATTRIBUTE_VALUE, "true")
                    }
                )
            }
        }
    }
}
