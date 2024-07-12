package dev.jkcarino.revanced.patches.google.gboard.fixes

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import dev.jkcarino.revanced.patches.google.gboard.detection.signature.BypassSignaturePatch
import dev.jkcarino.revanced.util.get

@Patch(
    name = "Apply AAPT workaround",
    description = "Applies workaround for AAPT to fix missing or unsupported resources. " +
        "This only applies to versions 14.1.x.x and later.",
    dependencies = [
        BypassSignaturePatch::class,
    ],
    compatiblePackages = [CompatiblePackage("com.google.android.inputmethod.latin")],
    use = true
)
object ApplyAaptWorkaroundPatch : ResourcePatch() {
    override fun execute(context: ResourceContext) {
        context.xmlEditor["res/xml/method.xml"].use { editor ->
            val document = editor.file
            val inputMethod = document["input-method"]

            // A new input method was introduced in Android 15 Developer Preview 2. However, the
            // current AAPT is unable to find this new attribute and is throwing an error.
            //
            // error: attribute android:supportsConnectionlessStylusHandwriting not found.
            // WARNING: error: failed linking file resources
            //
            // https://developer.android.com/sdk/api_diff/v-dp2-incr/changes/android.R.attr
            inputMethod.removeAttribute("android:supportsConnectionlessStylusHandwriting")
        }
    }
}
