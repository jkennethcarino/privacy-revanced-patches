package dev.jkcarino.revanced.patches.google.gboard.detection.signature

import app.revanced.patcher.patch.bytecodePatch
import dev.jkcarino.revanced.util.returnEarly

val bypassSignaturePatch = bytecodePatch(
    description = "Bypasses the signature verification checks for Gboard.",
) {
    compatibleWith("com.google.android.inputmethod.latin")

    execute {
        checkSignatureFingerprint.method.returnEarly(true)
    }
}
