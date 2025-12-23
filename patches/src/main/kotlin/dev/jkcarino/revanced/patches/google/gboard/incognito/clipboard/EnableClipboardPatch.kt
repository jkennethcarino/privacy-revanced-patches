package dev.jkcarino.revanced.patches.google.gboard.incognito.clipboard

import app.revanced.patcher.extensions.InstructionExtensions.removeInstructions
import app.revanced.patcher.patch.bytecodePatch
import dev.jkcarino.revanced.patches.google.gboard.detection.signature.bypassSignaturePatch

@Suppress("unused")
val enableClipboardPatch = bytecodePatch(
    name = "Enable clipboard in incognito",
    description = "Enables clipboard support in incognito mode.",
    use = false,
) {
    dependsOn(bypassSignaturePatch)

    compatibleWith("com.google.android.inputmethod.latin")

    execute {
        onPrimaryClipChangedFingerprint.method.apply {
            val patternMatch = onPrimaryClipChangedFingerprint.patternMatch!!
            val isIncognitoModeIndex = patternMatch.startIndex
            val returnVoidIndex = patternMatch.endIndex
            val instructionsToRemoveCount = (returnVoidIndex - isIncognitoModeIndex) + 1

            removeInstructions(
                index = isIncognitoModeIndex,
                count = instructionsToRemoveCount
            )
        }
    }
}
