package dev.jkcarino.revanced.patches.google.gboard.misc.incognito

import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction35c
import dev.jkcarino.revanced.patches.google.gboard.detection.signature.bypassSignaturePatch
import dev.jkcarino.revanced.util.returnEarly

@Suppress("unused")
val alwaysIncognitoModePatch = bytecodePatch(
    name = "Always-incognito mode for Gboard",
    description = "Always opens Gboard in incognito mode to disable typing history collection and personalization.",
    use = false,
) {
    dependsOn(bypassSignaturePatch)

    compatibleWith("com.google.android.inputmethod.latin")

    execute {
        val method = isIncognitoModeFingerprint.methodOrNull
            ?: isIncognitoModeV2Fingerprint.methodOrNull
            ?: isIncognitoModeInlinedFingerprint.methodOrNull
            ?: throw PatchException("Failed to force-enable incognito mode.")

        when (method) {
            isIncognitoModeInlinedFingerprint.methodOrNull -> {
                val patternResult = isIncognitoModeInlinedFingerprint.patternMatch!!
                val requestIncognitoModeIndex = patternResult.endIndex
                val isIncognitoModeIndex = patternResult.endIndex - 1

                val requestIncognitoModeInstruction = method.getInstruction<BuilderInstruction35c>(
                    index = requestIncognitoModeIndex
                )
                val isIncognitoModeRegister = requestIncognitoModeInstruction.registerD

                method.replaceInstruction(
                    index = isIncognitoModeIndex,
                    smaliInstruction = "const/4 v$isIncognitoModeRegister, 0x1"
                )
            }
            else -> {
                method.returnEarly(true)
            }
        }
    }
}
