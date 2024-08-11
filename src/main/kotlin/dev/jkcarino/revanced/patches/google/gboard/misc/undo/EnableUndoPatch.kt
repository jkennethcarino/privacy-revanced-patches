package dev.jkcarino.revanced.patches.google.gboard.misc.undo

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import dev.jkcarino.revanced.patches.google.gboard.detection.signature.BypassSignaturePatch
import dev.jkcarino.revanced.patches.google.gboard.misc.undo.fingerprints.UndoAccessPointFingerprint
import dev.jkcarino.revanced.util.exception

@Patch(
    name = "Enable Undo feature for Gboard",
    description = "Enables undo feature to quickly undo or correct typing mistakes.",
    dependencies = [
        BypassSignaturePatch::class,
    ],
    compatiblePackages = [CompatiblePackage("com.google.android.inputmethod.latin")],
    use = false
)
@Suppress("unused")
object EnableUndoPatch : BytecodePatch(
    setOf(UndoAccessPointFingerprint)
) {
    override fun execute(context: BytecodeContext) {
        UndoAccessPointFingerprint.result?.let { result ->
            val isEnabledIndex = result.scanResult.patternScanResult!!.endIndex

            result.mutableMethod.replaceInstructions(
                index = isEnabledIndex,
                smaliInstructions = "const/4 v1, 0x1"
            )
        } ?: throw UndoAccessPointFingerprint.exception
    }
}
