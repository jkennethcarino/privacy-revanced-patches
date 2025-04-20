package dev.jkcarino.revanced.patches.all.contentblocker.ads.meta

import app.revanced.patcher.patch.BytecodePatchContext
import app.revanced.patcher.patch.booleanOption
import dev.jkcarino.revanced.util.filterMethods
import dev.jkcarino.revanced.util.findMutableMethodOf
import dev.jkcarino.revanced.util.proxy
import dev.jkcarino.revanced.util.returnEarly

internal val disableMetaAudienceNetworkOption = booleanOption(
    key = "disableMetaAudienceNetwork",
    default = true,
    title = "Meta Audience Network",
    description = "Disable Banner, Medium rectangle, Interstitial, Native, Native Banner, Rewarded Video, and " +
        "Rewarded Interstitial ad formats."
)

internal fun BytecodePatchContext.applyMetaAudienceNetworkPatch() = buildList {
    val blockMethods = setOf(
        "loadAd",
        // Only present in interstitial ads
        "isAdLoaded",
        "show",
    )

    setOf(
        "Lcom/facebook/ads/AdView;",
        "Lcom/facebook/ads/NativeAdBase;",
        "Lcom/facebook/ads/RewardedInterstitialAd;",
        "Lcom/facebook/ads/RewardedVideoAd;",
    ).forEach { definingClass ->
        runCatching {
            val mutableClass = proxy(definingClass).mutableClass

            mutableClass
                .filterMethods { _, method -> method.name in blockMethods }
                .forEach { method ->
                    mutableClass
                        .findMutableMethodOf(method)
                        .returnEarly()
                }
        }.also(::add)
    }

    runCatching {
        initializeFingerprint.method.returnEarly()
    }.also(::add)
}
