package dev.jkcarino.revanced.patches.all.signature.pmshook

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotation.Patch
import dev.jkcarino.revanced.util.get
import dev.jkcarino.revanced.util.set

@Patch(description = "Replaces the sub-application with our SignatureHookApp.")
object ReplaceSubApplicationPatch : ResourcePatch() {
    private const val INTEGRATIONS_CLASS_NAME =
        "dev.jkcarino.revanced.integrations.all.signature.SignatureHookApp"

    internal lateinit var subApplicationClass: String

    override fun execute(context: ResourceContext) {
        context.xmlEditor["AndroidManifest.xml"].use { editor ->
            val document = editor.file
            val application = document["application"]

            subApplicationClass = application["android:name"]
            if (subApplicationClass == INTEGRATIONS_CLASS_NAME) {
                throw PatchException(
                    "You're trying to patch an app that has already been modified. " +
                        "This patch requires the original app to work properly."
                )
            }

            application["android:name"] = INTEGRATIONS_CLASS_NAME
        }
    }
}
