package dev.jkcarino.revanced.patches.all.misc.identity.advertising

import app.revanced.patcher.fingerprint

internal val getInfoInternalFingerprint = fingerprint {
    returns("L")
    strings(
        "Calling this from your main thread can lead to deadlock",
        "AdvertisingIdClient",
        "GMS remote exception",
        "Remote exception",
    )
}
