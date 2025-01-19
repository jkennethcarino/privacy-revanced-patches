package dev.jkcarino.revanced.patches.google.gboard.detection.signature

import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.bytecodePatch

val bypassSignaturePatch = bytecodePatch(
    description = "Bypasses the signature verification checks for Gboard.",
) {
    compatibleWith("com.google.android.inputmethod.latin")

    execute {
        checkSignatureFingerprint.method.addInstructions(
            index = 0,
            smaliInstructions = """
                const/4 v0, 0x1
                return v0
            """
        )
    }
}
