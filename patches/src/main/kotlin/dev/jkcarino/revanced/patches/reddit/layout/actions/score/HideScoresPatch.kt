package dev.jkcarino.revanced.patches.reddit.layout.actions.score

import app.revanced.patcher.Fingerprint
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.booleanOption
import app.revanced.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference
import dev.jkcarino.revanced.patches.reddit.shared.linkToStringFingerprint
import dev.jkcarino.revanced.patches.reddit.shared.util.updateClassField
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
        fun Fingerprint.updateScoreClassField(offset: Int, value: Any?) {
            val scoreIndex = this.stringMatches!!.last().index + offset
            val scoreInstruction = this.method.getInstruction<TwoRegisterInstruction>(scoreIndex)
            val scoreFieldReference = scoreInstruction.getReference<FieldReference>()!!

            updateClassField(
                classDef = this.classDef,
                fieldReference = scoreFieldReference,
                value = value
            )
        }

        if (!hidePostScores!! && !hideCommentScores!!) {
            return@execute Logger
                .getLogger(this::class.java.name)
                .warning("No score visibility options are enabled. No changes made.")
        }

        if (hidePostScores!!) {
            actionCellFragmentToStringFingerprint.updateScoreClassField(
                offset = 2,
                value = true
            )

            linkScoreFingerprints.forEach { (fingerprint, bool) ->
                fingerprint
                    .match(linkToStringFingerprint.classDef)
                    .method
                    .returnEarly(bool)
            }

            searchPostScoreToStringFingerprints.forEach { fingerprint ->
                fingerprint.updateScoreClassField(
                    offset = 2,
                    value = null
                )
            }
        }

        if (hideCommentScores!!) {
            searchCommentScoreToStringFingerprint.updateScoreClassField(
                offset = 2,
                value = null
            )

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
