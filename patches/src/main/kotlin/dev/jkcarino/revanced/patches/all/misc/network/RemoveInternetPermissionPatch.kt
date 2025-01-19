package dev.jkcarino.revanced.patches.all.misc.network

import app.revanced.patcher.patch.resourcePatch
import dev.jkcarino.revanced.patches.shared.resource.androidManifest
import dev.jkcarino.revanced.patches.shared.resource.removeUsesPermission

@Suppress("unused")
val removeInternetPermissionPatch = resourcePatch(
    name = "Remove internet permission",
    description = "Removes unnecessary internet permission from apps that can still work without internet access.",
    use = false,
) {
    execute {
        androidManifest {
            removeUsesPermission("""android\.permission\.INTERNET""")
        }
    }
}
