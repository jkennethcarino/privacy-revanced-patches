package dev.jkcarino.revanced.patches.all.contentblocker.ads.mintegral

import app.revanced.patcher.fingerprint
import com.android.tools.smali.dexlib2.AccessFlags

internal val mBridgeSdkInitFingerprint = fingerprint {
    accessFlags(AccessFlags.PRIVATE)
    returns("V")
    strings(
        "com.mbridge.msdk",
        "INIT FAIL",
    )
}
