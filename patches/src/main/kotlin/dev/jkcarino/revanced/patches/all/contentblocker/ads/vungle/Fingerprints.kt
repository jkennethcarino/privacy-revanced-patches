package dev.jkcarino.revanced.patches.all.contentblocker.ads.vungle

import app.revanced.patcher.fingerprint
import com.android.tools.smali.dexlib2.AccessFlags

internal val adInternalLoadFingerprint = fingerprint {
    accessFlags(AccessFlags.PUBLIC, AccessFlags.FINAL)
    returns("V")
    parameters("Ljava/lang/String;", "Ljava/lang/String;", "L")
    custom { method, _ ->
        method.definingClass == "Lcom/vungle/ads/internal/AdInternal;" &&
            method.name == "loadAd"
    }
}

internal val baseFullScreenAdLoadFingerprint = fingerprint {
    accessFlags(AccessFlags.PUBLIC)
    returns("V")
    parameters("Ljava/lang/String;")
    custom { method, _ ->
        method.definingClass == "Lcom/vungle/ads/BaseFullscreenAd;" &&
            method.name == "load"
    }
}

internal val baseAdLoadFingerprint = fingerprint {
    accessFlags(AccessFlags.PUBLIC)
    returns("V")
    parameters("Ljava/lang/String;")
    custom { method, _ ->
        method.definingClass == "Lcom/vungle/ads/BaseAd;" &&
            method.name == "load"
    }
}

internal val baseAdCanPlayAdFingerprint = fingerprint {
    accessFlags(AccessFlags.PUBLIC)
    returns("Ljava/lang/Boolean;")
    parameters()
    custom { method, _ ->
        method.definingClass == "Lcom/vungle/ads/BaseAd;" &&
            method.name == "canPlayAd"
    }
}
