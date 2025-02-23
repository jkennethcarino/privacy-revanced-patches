package dev.jkcarino.revanced.patches.all.contentblocker.ads.mytarget

import app.revanced.patcher.fingerprint
import com.android.tools.smali.dexlib2.AccessFlags

internal val myTargetManagerInitSdkFingerprint = fingerprint {
    accessFlags(AccessFlags.PUBLIC, AccessFlags.STATIC)
    returns("V")
    strings(
        "MyTarget cannot be initialized due to a null application context",
        "MyTarget initialization",
    )
}

internal val onAdLoadExecutorFingerprint = fingerprint {
    returns("V")
    strings(
        "AsyncCommand",
        "Can't use onAdLoadExecutor - sdk initialize not finished",
    )
}

internal val adLoaderFingerprints = setOf(
    "MyTargetView: Doesn't support multiple load",
    "BaseInterstitialAd: Interstitial/Rewarded doesn't support multiple load",
    "InstreamAd: Doesn't support multiple load",
    "InstreamAudioAd: Doesn't support multiple load",
    "NativeAd: Doesn't support multiple load",
    "NativeAppwallAd: Appwall ad doesn't support multiple load",
    "NativeBannerAd: Doesn't support multiple load",
    "NativeAdLoader: Invalid bannersCount < 1, bannersCount set to ",
    "NativeBannerAdLoader: Invalid bannersCount < 1, bannersCount set to ",
).map { string ->
    fingerprint {
        returns("V")
        strings(string)
    }
}

internal val promoCardRecyclerViewSetAdapterFingerprint = fingerprint {
    accessFlags(AccessFlags.PUBLIC)
    returns("V")
    strings(
        "PromoCardRecyclerView: You must use setPromoCardAdapter(PromoCardAdapter) method with custom CardRecyclerView",
    )
}
