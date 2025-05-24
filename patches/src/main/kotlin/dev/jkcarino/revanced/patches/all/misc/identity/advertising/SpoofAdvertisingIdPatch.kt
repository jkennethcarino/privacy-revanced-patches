package dev.jkcarino.revanced.patches.all.misc.identity.advertising

import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.extensions.InstructionExtensions.instructions
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import dev.jkcarino.revanced.util.getReference

@Suppress("unused")
val spoofAdvertisingIdPatch = bytecodePatch(
    name = "Spoof Advertising ID",
    description = "Spoofs the device's advertising ID with a string of zeros.",
    use = false,
) {
    execute {
        getInfoInternalFingerprint.method.apply {
            val advertisingIdClientInfo = returnType
            val advertisingIdClientInfoIndex = instructions
                .last { instruction ->
                    val reference = instruction
                        .getReference<MethodReference>()
                        ?: return@last false

                    reference.definingClass == advertisingIdClientInfo
                        && reference.name == "<init>"
                        && reference.returnType == "V"
                }
                .location
                .index

            val targetRegister =
                getInstruction<FiveRegisterInstruction>(advertisingIdClientInfoIndex)
                    .registerD

            val advertisingIdIndex = instructions
                .take(advertisingIdClientInfoIndex)
                .last { instruction ->
                    val index = instruction.location.index
                    val advertisingIdRegister =
                        (getInstruction(index) as? OneRegisterInstruction)
                            ?.registerA
                            ?: return@last false

                    advertisingIdRegister == targetRegister
                }
                .location
                .index

            replaceInstruction(
                index = advertisingIdIndex,
                smaliInstruction = """
                    const-string v$targetRegister, "00000000-0000-0000-0000-000000000000"
                """
            )
        }
    }
}
