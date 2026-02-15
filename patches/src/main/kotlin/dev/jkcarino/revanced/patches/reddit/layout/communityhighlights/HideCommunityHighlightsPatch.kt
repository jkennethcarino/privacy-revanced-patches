package dev.jkcarino.revanced.patches.reddit.layout.communityhighlights

import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.extensions.InstructionExtensions.instructions
import app.revanced.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference
import com.android.tools.smali.dexlib2.iface.reference.TypeReference
import dev.jkcarino.revanced.patches.reddit.misc.firebase.spoofCertificateHashPatch
import dev.jkcarino.revanced.patches.reddit.shared.util.updateClassField
import dev.jkcarino.revanced.util.getReference

@Suppress("unused")
val hideCommunityHighlightsPatch = bytecodePatch(
    name = "Hide community highlights",
    description = "Hides the community highlights section.",
    use = false,
) {
    dependsOn(spoofCertificateHashPatch)

    compatibleWith("com.reddit.frontpage")

    execute {
        val toStringFingerprint = subredditInfoByIdToStringFingerprint
        val highlightedPostsIndex = toStringFingerprint.stringMatches!!.last().index + 2
        val highlightedPostsInstruction =
            toStringFingerprint.method.getInstruction<TwoRegisterInstruction>(highlightedPostsIndex)
        val highlightedPostsFieldReference =
            highlightedPostsInstruction.getReference<FieldReference>()!!

        updateClassField(
            classDef = toStringFingerprint.classDef,
            fieldReference = highlightedPostsFieldReference,
            value = null
        )

        invokeFingerprint.method.apply {
            val uiStateInterface = loadedToStringFingerprint.classDef.interfaces.first()
            val communityHighlightsStateIndex =
                instructions.indexOfFirst { instruction ->
                    instruction.opcode == Opcode.CHECK_CAST
                        && instruction.getReference<TypeReference>()?.type == uiStateInterface
                }

            val unitFingerprint = unitFingerprint.match(unitToStringFingerprint.classDef)
            val unitIndex = unitFingerprint.patternMatch!!.endIndex
            val unitInstruction =
                unitFingerprint.method.getInstruction<OneRegisterInstruction>(unitIndex)
            val unitFieldReference = unitInstruction.getReference<FieldReference>()!!

            val unitDefiningClass = unitFieldReference.definingClass
            val unitFieldName = unitFieldReference.name
            val unitFieldType = unitFieldReference.type

            addInstructions(
                index = communityHighlightsStateIndex,
                smaliInstructions = """
                    sget-object v1, $unitDefiningClass->$unitFieldName:$unitFieldType
                    return-object v1
                """
            )
        }
    }
}
