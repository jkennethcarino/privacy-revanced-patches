package dev.jkcarino.revanced.patches.reddit.layout.screenshotbanner

import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import dev.jkcarino.revanced.util.returnEarly

@Suppress("unused")
val disableScreenshotBannerPatch = bytecodePatch(
    name = "Disable screenshot banner",
    description = "Disables the banner that shows up after taking a screenshot.",
    use = true
) {
    compatibleWith("com.reddit.frontpage")

    execute {
        onScreenCapturedFingerprint.methodOrNull?.returnEarly()

        listOf(
            screenshotBannerInvokeSuspendFingerprint,
            screenshotTakenBannerInvokeSuspendFingerprint
        ).forEach { fingerprint ->
            fingerprint.method.apply {
                val booleanIndex = fingerprint.patternMatch!!.endIndex - 1
                val booleanRegister =
                    getInstruction<OneRegisterInstruction>(booleanIndex).registerA

                replaceInstruction(
                    index = booleanIndex,
                    smaliInstruction = """
                         sget-object v$booleanRegister, Ljava/lang/Boolean;->FALSE:Ljava/lang/Boolean;
                    """
                )
            }
        }
    }
}
