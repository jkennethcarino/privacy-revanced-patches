package dev.jkcarino.revanced.patches.google.gboard.misc.incognito

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import dev.jkcarino.revanced.patches.google.gboard.detection.signature.BypassSignaturePatch
import dev.jkcarino.revanced.patches.google.gboard.misc.incognito.fingerprints.IsIncognitoModeFingerprint
import dev.jkcarino.revanced.util.exception

@Patch(
    name = "Always-incognito mode for Gboard",
    description = "Always opens Gboard in incognito mode to disable typing history collection and personalization. " +
        "This requires the original, unmodified app to work properly.",
    dependencies = [
        BypassSignaturePatch::class,
    ],
    compatiblePackages = [CompatiblePackage("com.google.android.inputmethod.latin")],
    use = false
)
@Suppress("unused")
object AlwaysIncognitoModePatch : BytecodePatch(
    setOf(IsIncognitoModeFingerprint)
) {
    override fun execute(context: BytecodeContext) {
        IsIncognitoModeFingerprint.result?.mutableMethod?.addInstructions(
            index = 0,
            smaliInstructions = """
                const/4 v0, 0x1
                return v0
            """
        ) ?: throw IsIncognitoModeFingerprint.exception
    }
}
