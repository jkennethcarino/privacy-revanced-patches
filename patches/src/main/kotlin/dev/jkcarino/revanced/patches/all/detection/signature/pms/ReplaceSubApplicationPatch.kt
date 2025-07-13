package dev.jkcarino.revanced.patches.all.detection.signature.pms

import app.revanced.patcher.patch.resourcePatch
import dev.jkcarino.revanced.patches.shared.resource.ANDROID_NAME_ATTR
import dev.jkcarino.revanced.patches.shared.resource.APPLICATION_NODE
import dev.jkcarino.revanced.patches.shared.resource.androidManifest
import dev.jkcarino.revanced.util.get
import dev.jkcarino.revanced.util.set

private const val EXTENSION_CLASS_NAME =
    "dev.jkcarino.extension.all.detection.signature.pms.SignatureHookApp"

val replaceSubApplicationPatch = resourcePatch(
    description = "Sets the sub-application with our SignatureHookApp."
) {
    execute {
        androidManifest {
            val application = this[APPLICATION_NODE]
            val subApplicationName = application[ANDROID_NAME_ATTR]

            if (subApplicationName.isEmpty()) {
                application[ANDROID_NAME_ATTR] = EXTENSION_CLASS_NAME
            }
        }
    }
}
