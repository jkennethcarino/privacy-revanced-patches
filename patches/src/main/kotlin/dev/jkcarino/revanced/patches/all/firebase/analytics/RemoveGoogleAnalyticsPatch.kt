package dev.jkcarino.revanced.patches.all.firebase.analytics

import app.revanced.patcher.patch.resourcePatch
import dev.jkcarino.revanced.patches.shared.resource.androidManifest
import dev.jkcarino.revanced.patches.shared.resource.removeReceiver
import dev.jkcarino.revanced.patches.shared.resource.removeService

val removeGoogleAnalyticsPatch = resourcePatch(
    description = "Removes Google Analytics's broadcast receivers and services.",
) {
    execute {
        androidManifest {
            removeReceiver("""com\.google\.android\.gms\.analytics\..+Receiver$""")
            removeService("""com\.google\.android\.gms\.analytics\..+Service$""")
        }
    }
}
