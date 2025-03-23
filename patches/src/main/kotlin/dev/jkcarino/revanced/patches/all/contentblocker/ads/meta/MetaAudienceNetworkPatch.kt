package dev.jkcarino.revanced.patches.all.contentblocker.ads.meta

import app.revanced.patcher.patch.BytecodePatchContext
import app.revanced.patcher.patch.booleanOption
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import dev.jkcarino.revanced.util.returnEarly
import dev.jkcarino.revanced.util.transformMethods

internal val disableMetaAudienceNetworkOption by lazy {
    booleanOption(
        key = "disableMetaAudienceNetwork",
        default = true,
        title = "Meta Audience Network",
        description = "Disable Banner, Medium rectangle, Interstitial, Native, Native Banner, Rewarded Video, and " +
            "Rewarded Interstitial ad formats."
    )
}

internal fun BytecodePatchContext.applyMetaAudienceNetworkPatch() {
    val blockMethods = setOf(
        "loadAd",
        // Only present in interstitial ads
        "isAdLoaded",
        "show"
    )

    setOf(
        "Lcom/facebook/ads/AdView;",
        "Lcom/facebook/ads/NativeAdBase;",
        "Lcom/facebook/ads/RewardedInterstitialAd;",
        "Lcom/facebook/ads/RewardedVideoAd;",
    ).forEach { definingClass ->
        transformMethods(
            definingClass = definingClass,
            predicate = { _, method -> method.name in blockMethods },
            transform = MutableMethod::returnEarly
        )
    }

    initializeFingerprint.method.returnEarly()
}
