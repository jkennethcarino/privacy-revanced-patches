package dev.jkcarino.revanced.patches.all.contentblocker.ads.applovin

import app.revanced.patcher.fingerprint
import com.android.tools.smali.dexlib2.AccessFlags

internal val interstitialAdDialogToStringFingerprint = fingerprint {
    accessFlags(AccessFlags.PUBLIC)
    returns("Ljava/lang/String;")
    strings("AppLovinInterstitialAdDialog{}")
}
