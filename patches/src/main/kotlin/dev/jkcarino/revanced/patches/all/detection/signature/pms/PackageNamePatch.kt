package dev.jkcarino.revanced.patches.all.detection.signature.pms

import app.revanced.patcher.patch.resourcePatch
import dev.jkcarino.revanced.patches.shared.resource.MANIFEST_NODE
import dev.jkcarino.revanced.patches.shared.resource.androidManifest
import dev.jkcarino.revanced.util.get

internal lateinit var appPackageName: String
    private set

val packageNamePatch = resourcePatch(
    description = "Extracts the package name of the app."
) {
    execute {
        androidManifest {
            val manifest = this[MANIFEST_NODE]
            appPackageName = manifest["package"]
        }
    }
}
