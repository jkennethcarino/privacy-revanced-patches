package dev.jkcarino.revanced.patches.all.firebase.analytics

import app.revanced.patcher.patch.resourcePatch
import dev.jkcarino.revanced.patches.shared.resource.androidManifest
import dev.jkcarino.revanced.patches.shared.resource.metaData

@Suppress("unused")
val deactivateFirebaseAnalyticsPatch = resourcePatch(
    name = "Deactivate Firebase Analytics",
    description = "Deactivates Firebase Analytics and removes its associated broadcast receivers and services.",
    use = false,
) {
    dependsOn(
        removeAdsServicesPatch,
        removeAdvertisingIdPatch,
        removeAppMeasurementPatch,
        removeGoogleAnalyticsPatch,
    )

    execute {
        androidManifest {
            metaData("firebase_analytics_collection_deactivated" to "true")
        }
    }
}
