package dev.jkcarino.revanced.patches.all.contentblocker.ads.pangle

import app.revanced.patcher.fingerprint
import com.android.tools.smali.dexlib2.AccessFlags

internal val sdkLoadAdFactoryFingerprint = fingerprint {
    accessFlags(AccessFlags.PUBLIC, AccessFlags.STATIC)
    returns("L")
    strings(
        "SDK disable",
        "SDK load ad factory should not be null",
    )
}
