package dev.jkcarino.revanced.patches.gboard.signature

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotation.Patch
import dev.jkcarino.revanced.util.firstElementByTagName

@Patch(description = "Replaces ImeLatinApp with our SignatureHookApp.")
object ReplaceImeLatinAppPatch : ResourcePatch() {
    private const val INTEGRATIONS_CLASS_NAME =
        "dev.jkcarino.revanced.integrations.gboard.SignatureHookApp"

    override fun execute(context: ResourceContext) {
        context.xmlEditor["AndroidManifest.xml"].use { editor ->
            val document = editor.file

            val application = document.firstElementByTagName("application")
            application.setAttribute("android:name", INTEGRATIONS_CLASS_NAME)
        }
    }
}
