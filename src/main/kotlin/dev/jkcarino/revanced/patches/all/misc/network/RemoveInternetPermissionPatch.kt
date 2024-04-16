package dev.jkcarino.revanced.patches.all.misc.network

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotation.Patch
import dev.jkcarino.revanced.util.asElementSequence
import dev.jkcarino.revanced.util.get

@Patch(
    name = "Remove internet permission",
    description = "Removes unnecessary internet permission from apps that can function without internet access.",
    use = false
)
@Suppress("unused")
object RemoveInternetPermissionPatch : ResourcePatch() {
    override fun execute(context: ResourceContext) {
        context.xmlEditor["AndroidManifest.xml"].use { editor ->
            val document = editor.file
            val manifest = document["manifest"]

            document.getElementsByTagName("uses-permission")
                .asElementSequence()
                .firstOrNull { it["android:name"] == "android.permission.INTERNET" }
                ?.let { internetPermission ->
                    manifest.removeChild(internetPermission)
                }
        }
    }
}
