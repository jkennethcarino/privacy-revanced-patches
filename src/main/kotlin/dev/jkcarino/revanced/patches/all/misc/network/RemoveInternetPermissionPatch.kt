package dev.jkcarino.revanced.patches.all.misc.network

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotation.Patch
import dev.jkcarino.revanced.util.filterToElements

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

            document.getElementsByTagName("uses-permission")
                .filterToElements()
                .firstOrNull { it.getAttribute("android:name") == "android.permission.INTERNET" }
                ?.let { internetPermission ->
                    internetPermission.parentNode.removeChild(internetPermission)
                }
        }
    }
}
