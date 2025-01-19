package dev.jkcarino.revanced.patches.all.detection.signature.pms

import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.resourcePatch
import dev.jkcarino.revanced.patches.shared.resource.ANDROID_NAME_ATTR
import dev.jkcarino.revanced.patches.shared.resource.APPLICATION_NODE
import dev.jkcarino.revanced.patches.shared.resource.androidManifest
import dev.jkcarino.revanced.util.get
import dev.jkcarino.revanced.util.set

private const val EXTENSION_CLASS_NAME =
    "dev.jkcarino.extension.all.detection.signature.pms.SignatureHookApp"

internal lateinit var originalSubApplicationClass: String
    private set

val replaceSubApplicationPatch = resourcePatch(
    description = "Replaces the sub-application with our SignatureHookApp."
) {
    execute {
        androidManifest {
            val application = this[APPLICATION_NODE]

            originalSubApplicationClass = application[ANDROID_NAME_ATTR]
            if (originalSubApplicationClass == EXTENSION_CLASS_NAME) {
                throw PatchException(
                    "You're trying to patch an app that has already been modified. " +
                        "This patch requires the original app to work properly."
                )
            }

            application[ANDROID_NAME_ATTR] = EXTENSION_CLASS_NAME
        }
    }
}
