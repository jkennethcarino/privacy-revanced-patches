package dev.jkcarino.revanced.patches.all.firebase.analytics

import app.revanced.patcher.patch.resourcePatch
import dev.jkcarino.revanced.patches.shared.resource.androidManifest
import dev.jkcarino.revanced.patches.shared.resource.removeUsesPermission

val removeAdvertisingIdPatch = resourcePatch(
    description = "Removes the Advertising ID permission.",
) {
    execute {
        androidManifest {
            removeUsesPermission("""com\.google\.android\.gms\.permission\.AD_ID""")
        }
    }
}
