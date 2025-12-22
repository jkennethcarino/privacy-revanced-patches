package dev.jkcarino.revanced.patches.all.contentblocker.ads.unity

import app.revanced.patcher.fingerprint
import com.android.tools.smali.dexlib2.AccessFlags

internal val unityAdsIsInitializedFingerprint = fingerprint {
    accessFlags(AccessFlags.PUBLIC, AccessFlags.STATIC)
    returns("V")
    custom { method, _ ->
        method.name == "isInitialized"
            && method.definingClass == "Lcom/unity3d/ads/UnityAds;"
    }
}

internal val unityServicesInitializeFingerprint = fingerprint {
    accessFlags(AccessFlags.PUBLIC, AccessFlags.STATIC, AccessFlags.FINAL)
    returns("V")
    strings("Unity Services environment check OK")
}
