package dev.jkcarino.revanced.patches.google.gboard.misc.incognito

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction35c
import dev.jkcarino.revanced.patches.google.gboard.detection.signature.BypassSignaturePatch
import dev.jkcarino.revanced.patches.google.gboard.misc.incognito.fingerprints.IsIncognitoModeFingerprint
import dev.jkcarino.revanced.patches.google.gboard.misc.incognito.fingerprints.IsIncognitoModeInlinedFingerprint

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
    setOf(
        IsIncognitoModeFingerprint,
        IsIncognitoModeInlinedFingerprint, // since 14.4.06.646482735 beta
    )
) {
    override fun execute(context: BytecodeContext) {
        val isIncognitoModeFingerprintResult = IsIncognitoModeFingerprint.result
        val isIncognitoModeInlinedFingerprintResult = IsIncognitoModeInlinedFingerprint.result

        isIncognitoModeFingerprintResult
            ?: isIncognitoModeInlinedFingerprintResult
            ?: throw PatchException("Failed to force-enable incognito mode.")

        isIncognitoModeFingerprintResult?.mutableMethod?.addInstructions(
            index = 0,
            smaliInstructions = """
                const/4 v0, 0x1
                return v0
            """
        )

        isIncognitoModeInlinedFingerprintResult?.let { result ->
            val patternScanResult = result.scanResult.patternScanResult!!
            val requestIncognitoModeIndex = patternScanResult.endIndex
            val isIncognitoModeIndex = patternScanResult.endIndex - 1

            result.mutableMethod.apply {
                val requestIncognitoModeInstruction = getInstruction<BuilderInstruction35c>(
                    index = requestIncognitoModeIndex
                )
                val isIncognitoModeRegister = requestIncognitoModeInstruction.registerD

                replaceInstruction(
                    index = isIncognitoModeIndex,
                    smaliInstruction = "const/4 v$isIncognitoModeRegister, 0x1"
                )
            }
        }
    }
}
