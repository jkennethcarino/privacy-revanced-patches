package dev.jkcarino.revanced.patches.reddit.layout.actions.score

import app.revanced.patcher.fingerprint

internal val actionCellFragmentToStringFingerprint = fingerprint {
    returns("Ljava/lang/String;")
    parameters()
    strings(
        ", isScoreHidden=",
        "ActionCellFragment(id=",
    )
}

internal val linkScoreFingerprints =
    listOf(
        Triple("getScore", "I", false),
        Triple("getHideScore", "Z", true),
    ).map { (methodName, returnType, bool) ->
        val fingerprint = fingerprint {
            returns(returnType)
            parameters()
            custom { method, _ ->
                method.name == methodName
            }
        }
        fingerprint to bool
    }

internal val searchPostScoreToStringFingerprints =
    setOf(
        "PostContentFragment(__typename=",
        "SearchPostContentFragment(__typename=",
    ).map { prefix ->
        fingerprint {
            returns("Ljava/lang/String;")
            parameters()
            strings(prefix, ", score=")
        }
    }

internal val searchCommentScoreToStringFingerprint = fingerprint {
    returns("Ljava/lang/String;")
    parameters()
    strings(
        "SearchComment(commentId=",
        ", score=",
    )
}
