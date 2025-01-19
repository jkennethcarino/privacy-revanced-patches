package dev.jkcarino.revanced.patches.google.gboard.misc.ocr

import app.revanced.patcher.extensions.InstructionExtensions.replaceInstructions
import app.revanced.patcher.patch.bytecodePatch
import dev.jkcarino.revanced.patches.google.gboard.detection.signature.bypassSignaturePatch

@Suppress("unused")
val enableOcrPatch = bytecodePatch(
    name = "Enable OCR feature for Gboard",
    description = "Enables OCR feature to extract text from images and insert it into text fields.",
    use = false,
) {
    dependsOn(bypassSignaturePatch)

    compatibleWith("com.google.android.inputmethod.latin")

    execute {
        ocrAccessPointFingerprint.method.apply {
            val isEnabledIndex = ocrAccessPointFingerprint.patternMatch!!.endIndex

            replaceInstructions(
                index = isEnabledIndex,
                smaliInstructions = "const/4 v1, 0x1"
            )
        }
    }
}
