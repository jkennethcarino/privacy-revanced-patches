package dev.jkcarino.revanced.patches.all.firebase.analytics

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotation.Patch
import dev.jkcarino.revanced.util.asElementSequence
import dev.jkcarino.revanced.util.createElement
import dev.jkcarino.revanced.util.get
import dev.jkcarino.revanced.util.set

@Patch(
    name = "Deactivate Firebase Analytics",
    description = "Deactivates Firebase Analytics and removes its associated broadcast receivers and services.",
    dependencies = [
        RemoveAdServicesPatch::class,
        RemoveAdvertisingIdPatch::class,
        RemoveAppMeasurementPatch::class,
        RemoveGoogleAnalyticsPatch::class,
    ],
    use = false
)
@Suppress("unused")
object DeactivateFirebaseAnalyticsPatch : ResourcePatch() {
    private const val META_DATA_TAG = "meta-data"
    private const val ATTRIBUTE_NAME = "android:name"
    private const val ATTRIBUTE_VALUE = "android:value"
    private const val FIREBASE_ANALYTICS_DEACTIVATED = "firebase_analytics_collection_deactivated"

    override fun execute(context: ResourceContext) {
        context.xmlEditor["AndroidManifest.xml"].use { editor ->
            val document = editor.file
            val application = document["application"]

            val firebaseAnalyticsMetaData = application.getElementsByTagName(META_DATA_TAG)
                .asElementSequence()
                .firstOrNull { it[ATTRIBUTE_NAME] == FIREBASE_ANALYTICS_DEACTIVATED }
                ?.setAttribute(ATTRIBUTE_VALUE, "true")

            if (firebaseAnalyticsMetaData == null) {
                application.appendChild(
                    document.createElement(META_DATA_TAG) {
                        this[ATTRIBUTE_NAME] = FIREBASE_ANALYTICS_DEACTIVATED
                        this[ATTRIBUTE_VALUE] = "true"
                    }
                )
            }
        }
    }
}
