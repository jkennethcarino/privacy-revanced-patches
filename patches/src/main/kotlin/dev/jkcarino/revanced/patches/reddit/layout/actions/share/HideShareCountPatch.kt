package dev.jkcarino.revanced.patches.reddit.layout.actions.share

import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference
import dev.jkcarino.revanced.patches.reddit.shared.linkToStringFingerprint
import dev.jkcarino.revanced.patches.reddit.shared.util.updateClassField
import dev.jkcarino.revanced.util.getReference
import dev.jkcarino.revanced.util.returnEarly

@Suppress("unused")
val hideShareCountPatch = bytecodePatch(
    name = "Hide share count",
    description = "Hides the share count on Reddit posts.",
    use = false,
) {
    compatibleWith("com.reddit.frontpage")

    execute {
        val shareCountMatch = actionCellFragmentToStringFingerprint.stringMatches!!.last()
        val shareCountIndex = shareCountMatch.index + 2
        val shareCountInstruction = actionCellFragmentToStringFingerprint
            .method
            .getInstruction<TwoRegisterInstruction>(shareCountIndex)

        val actionCellFragmentClassDef = actionCellFragmentToStringFingerprint.classDef
        val shareCountFieldReference = shareCountInstruction.getReference<FieldReference>()!!

        updateClassField(
            classDef = actionCellFragmentClassDef,
            fieldReference = shareCountFieldReference,
            value = null
        )

        getShareCountFingerprint
            .match(linkToStringFingerprint.classDef)
            .method
            .returnEarly()
    }
}
