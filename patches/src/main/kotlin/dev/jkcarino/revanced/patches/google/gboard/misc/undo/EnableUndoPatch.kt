package dev.jkcarino.revanced.patches.google.gboard.misc.undo

import app.revanced.patcher.extensions.InstructionExtensions.replaceInstructions
import app.revanced.patcher.patch.bytecodePatch
import dev.jkcarino.revanced.patches.google.gboard.detection.signature.bypassSignaturePatch

@Suppress("unused")
val enableUndoPatch = bytecodePatch(
    name = "Enable Undo feature for Gboard",
    description = "Enables undo feature to quickly undo or correct typing mistakes.",
    use = false,
) {
    dependsOn(bypassSignaturePatch)

    compatibleWith("com.google.android.inputmethod.latin")

    execute {
        undoAccessPointFingerprint.method.apply {
            val isEnabledIndex = undoAccessPointFingerprint.patternMatch!!.endIndex

            replaceInstructions(
                index = isEnabledIndex,
                smaliInstructions = "const/4 v1, 0x1"
            )
        }
    }
}
