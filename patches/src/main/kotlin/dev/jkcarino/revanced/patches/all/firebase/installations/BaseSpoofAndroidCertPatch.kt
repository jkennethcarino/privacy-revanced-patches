package dev.jkcarino.revanced.patches.all.firebase.installations

import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.instructions
import app.revanced.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.BuilderInstruction
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import dev.jkcarino.revanced.util.getReference

fun baseSpoofAndroidCertPatch(certificateHash: () -> String) = bytecodePatch {
    execute {
        fun BuilderInstruction.isAddRequestPropertyCall(): Boolean {
            return opcode == Opcode.INVOKE_VIRTUAL
                && getReference<MethodReference>()?.name == "addRequestProperty"
        }

        openHttpUrlConnectionFingerprint.method.apply {
            val certificateHash = certificateHash().uppercase()
            val xAndroidCertIndex =
                openHttpUrlConnectionFingerprint.stringMatches!!.first().index

            val addRequestPropertyInstruction = instructions
                .slice(xAndroidCertIndex until instructions.size)
                .first(BuilderInstruction::isAddRequestPropertyCall)

            val valueRegister =
                (addRequestPropertyInstruction as FiveRegisterInstruction).registerE

            addInstruction(
                index = addRequestPropertyInstruction.location.index,
                smaliInstructions = """
                    const-string v$valueRegister, "$certificateHash"
                """
            )
        }
    }
}
