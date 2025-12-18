package dev.jkcarino.revanced.patches.reddit.shared

import app.revanced.patcher.fingerprint

internal val linkToStringFingerprint = fingerprint {
    returns("Ljava/lang/String;")
    parameters()
    strings("Link(id=")
}
