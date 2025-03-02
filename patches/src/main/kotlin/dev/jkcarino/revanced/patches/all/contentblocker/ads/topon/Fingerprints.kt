package dev.jkcarino.revanced.patches.all.contentblocker.ads.topon

import app.revanced.patcher.fingerprint
import com.android.tools.smali.dexlib2.AccessFlags

internal val atSdkInitFingerprint = fingerprint {
    accessFlags(AccessFlags.PUBLIC, AccessFlags.STATIC)
    returns("V")
    strings("init: Context is null!", "anythink")
}

internal val atRewardedVideoAdLoadManagerShowFingerprint = fingerprint {
    returns("V")
    strings("4001", "", "No Cache.")
}
