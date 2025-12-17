package dev.jkcarino.revanced.patches.reddit.layout.actions.score

import app.revanced.patcher.Fingerprint
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.extensions.InstructionExtensions.instructions
import app.revanced.patcher.patch.booleanOption
import app.revanced.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference
import dev.jkcarino.revanced.util.filterMethods
import dev.jkcarino.revanced.util.findMutableMethodOf
import dev.jkcarino.revanced.util.getReference
import dev.jkcarino.revanced.util.proxy
import dev.jkcarino.revanced.util.returnEarly
import java.util.logging.Logger

@Suppress("unused")
val hideScoresPatch = bytecodePatch(
    name = "Hide upvote scores",
    description = "Hides the scores on Reddit posts and comments.",
    use = false,
) {
    compatibleWith("com.reddit.frontpage")

    val hidePostScores by booleanOption(
        key = "hidePostScores",
        default = true,
        title = "Hide post scores",
        description = "Removes scores from posts in feeds and search results.",
    )

    val hideCommentScores by booleanOption(
        key = "hideCommentScores",
        default = true,
        title = "Hide comment scores",
        description = "Removes scores from comments in threads and search results.",
    )

    execute {
        fun Fingerprint.updateScoreField(
            offset: Int,
            bool: Boolean = false,
        ) {
            val bool = if (bool) "0x1" else "0x0"
            val scoreIndex = this.stringMatches!!.last().index + offset
            val scoreInstruction = this.method.getInstruction<TwoRegisterInstruction>(scoreIndex)
            val scoreFieldReference = scoreInstruction.getReference<FieldReference>()!!

            val constructor = proxy(this.classDef)
                .mutableClass
                .methods
                .first { method -> method.name == "<init>" }

            val scoreFieldIndex = constructor
                .instructions
                .indexOfFirst { instruction ->
                    val fieldReference = instruction
                        .getReference<FieldReference>()
                        ?: return@indexOfFirst false

                    fieldReference.definingClass == scoreFieldReference.definingClass
                        && fieldReference.name == scoreFieldReference.name
                        && fieldReference.type == scoreFieldReference.type
                }
            val scoreParamRegister = constructor
                .getInstruction<TwoRegisterInstruction>(scoreFieldIndex)
                .registerA

            constructor.addInstruction(
                index = scoreFieldIndex,
                smaliInstructions = "const/4 v$scoreParamRegister, $bool"
            )
        }

        if (!hidePostScores!! && !hideCommentScores!!) {
            return@execute Logger
                .getLogger(this::class.java.name)
                .warning("No score visibility options are enabled. No changes made.")
        }

        if (hidePostScores!!) {
            constructorFingerprint
                .match(actionCellFragmentToStringFingerprint.classDef)
                .method
                .apply {
                    val isScoreHiddenIndex = constructorFingerprint.patternMatch!!.endIndex
                    val isScoreHiddenFieldInstruction =
                        getInstruction<TwoRegisterInstruction>(isScoreHiddenIndex)
                    val isScoreHiddenParamRegister = isScoreHiddenFieldInstruction.registerA

                    addInstruction(
                        index = isScoreHiddenIndex,
                        smaliInstructions = "const/4 v$isScoreHiddenParamRegister, 0x1"
                    )
                }

            linkScoreFingerprints.forEach { (fingerprint, bool) ->
                fingerprint
                    .match(linkToStringFingerprint.classDef)
                    .method
                    .returnEarly(bool)
            }

            searchPostScoreToStringFingerprints.forEach { fingerprint ->
                fingerprint.updateScoreField(offset = 2)
            }
        }

        if (hideCommentScores!!) {
            searchCommentScoreToStringFingerprint.updateScoreField(offset = 2)

            val scoreHiddenMethods = setOf(
                "getScoreHidden",
                "getIsScoreHidden",
                "isScoreHidden",
            )
            classes
                .filter { classDef ->
                    classDef.interfaces.any { it.endsWith("/AnalyticableComment;") }
                }
                .filterMethods { _, method ->
                    method.name in scoreHiddenMethods
                }
                .forEach { method ->
                    proxy(method.definingClass)
                        .mutableClass
                        .findMutableMethodOf(method)
                        .returnEarly(true)
                }
        }
    }
}
