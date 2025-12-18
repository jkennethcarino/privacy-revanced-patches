package dev.jkcarino.revanced.patches.reddit.layout.actions.share

import app.revanced.patcher.fingerprint

internal val actionCellFragmentToStringFingerprint = fingerprint {
    returns("Ljava/lang/String;")
    parameters()
    strings(
        "ActionCellFragment(id=",
        ", shareCount=",
    )
}

internal val getShareCountFingerprint = fingerprint {
    returns("Ljava/lang/Long;")
    parameters()
    custom { method, _ ->
        method.name == "getShareCount"
    }
}
