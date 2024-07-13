package dev.jkcarino.revanced.patches.google.gboard.detection.signature

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import dev.jkcarino.revanced.patches.google.gboard.detection.signature.fingerprints.CheckSignatureFingerprint
import dev.jkcarino.revanced.util.exception

@Patch(
    description = "Bypasses the signature verification checks for Gboard",
    compatiblePackages = [CompatiblePackage("com.google.android.inputmethod.latin")],
    use = true
)
object BypassSignaturePatch : BytecodePatch(
    setOf(CheckSignatureFingerprint)
) {
    override fun execute(context: BytecodeContext) {
        CheckSignatureFingerprint.result?.mutableMethod?.addInstructions(
            index = 0,
            smaliInstructions = """
                const/4 v0, 0x1
                return v0
            """
        ) ?: throw CheckSignatureFingerprint.exception
    }
}
