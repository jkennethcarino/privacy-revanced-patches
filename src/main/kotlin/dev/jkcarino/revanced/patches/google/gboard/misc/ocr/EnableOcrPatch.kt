package dev.jkcarino.revanced.patches.google.gboard.misc.ocr

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import dev.jkcarino.revanced.patches.google.gboard.detection.signature.BypassSignaturePatch
import dev.jkcarino.revanced.patches.google.gboard.misc.ocr.fingerprints.OcrAccessPointFingerprint
import dev.jkcarino.revanced.util.exception

@Patch(
    name = "Enable OCR feature for Gboard",
    description = "Enables OCR feature to extract text from images and insert it into text fields.",
    dependencies = [
        BypassSignaturePatch::class,
    ],
    compatiblePackages = [CompatiblePackage("com.google.android.inputmethod.latin")],
    use = false
)
@Suppress("unused")
object EnableOcrPatch : BytecodePatch(
    setOf(OcrAccessPointFingerprint)
) {
    override fun execute(context: BytecodeContext) {
        OcrAccessPointFingerprint.result?.let { result ->
            val isEnabledIndex = result.scanResult.patternScanResult!!.endIndex

            result.mutableMethod.replaceInstructions(
                index = isEnabledIndex,
                smaliInstructions = "const/4 v1, 0x1"
            )
        } ?: throw OcrAccessPointFingerprint.exception
    }
}
