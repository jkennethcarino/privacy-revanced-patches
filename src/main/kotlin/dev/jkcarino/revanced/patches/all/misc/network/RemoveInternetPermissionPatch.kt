package dev.jkcarino.revanced.patches.all.misc.network

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotation.Patch
import org.w3c.dom.Element

@Patch(
    name = "Remove internet permission",
    description = "Removes unnecessary internet permission from apps that can function without internet access.",
    use = false
)
@Suppress("unused")
object RemoveInternetPermissionPatch : ResourcePatch() {
    override fun execute(context: ResourceContext) {
        context.xmlEditor["AndroidManifest.xml"].use { editor ->
            val usesPermissions = editor.file.getElementsByTagName("uses-permission")

            for (i in 0 until usesPermissions.length) {
                val element = usesPermissions.item(i) as? Element ?: continue
                if (element.getAttribute("android:name") == "android.permission.INTERNET") {
                    element.parentNode.removeChild(element)
                    break
                }
            }
        }
    }
}
