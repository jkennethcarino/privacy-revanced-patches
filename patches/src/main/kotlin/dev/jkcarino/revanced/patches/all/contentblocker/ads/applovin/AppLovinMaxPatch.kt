package dev.jkcarino.revanced.patches.all.contentblocker.ads.applovin

import app.revanced.patcher.patch.BytecodePatchContext
import app.revanced.patcher.patch.booleanOption
import dev.jkcarino.revanced.util.filterMethods
import dev.jkcarino.revanced.util.findMutableMethodOf
import dev.jkcarino.revanced.util.proxy
import dev.jkcarino.revanced.util.returnEarly

internal val disableAppLovinMaxOption by lazy {
    booleanOption(
        key = "disableAppLovinMax",
        default = true,
        title = "AppLovin MAX",
        description = "Disable App Open, Banner & MREC, Interstitial, Native, and Rewarded ad formats."
    )
}

internal fun BytecodePatchContext.applyAppLovinMaxPatch() = buildList {
    val appLovinSdkBlockMethods = setOf(
        "initialize",
        "initializeSdk",
        "isInitialized",
    )
    runCatching {
        val mutableClass =
            proxy("Lcom/applovin/sdk/AppLovinSdk;")
                .mutableClass

        mutableClass
            .filterMethods { _, method -> method.name in appLovinSdkBlockMethods }
            .forEach { method ->
                mutableClass
                    .findMutableMethodOf(method)
                    .returnEarly()
            }
    }.also(::add)

    val interstitialAdDialogClassDef =
        interstitialAdDialogToStringFingerprint.originalClassDefOrNull

    val blockMethods = setOf(
        "isReady",
        "loadAd",
        "render",
        "show",
        "showAd",
        // Only present in MediationServiceImpl
        "loadThirdPartyMediatedAd",
        // Only present in MediationServiceImpl and MaxFullscreenAdImpl
        "showFullscreenAd",
        // Only present in MaxAdView
        "startAutoRefresh",
        "stopAutoRefresh",
        // Only present in MaxNativeAdView
        "renderCustomNativeAdView",
        // Only present in AppLovinInterstitialAdDialog
        "showAndRender",
    )
    setOfNotNull(
        interstitialAdDialogClassDef?.type,
        "Lcom/applovin/impl/mediation/MediationServiceImpl;",
        "Lcom/applovin/impl/mediation/ads/MaxAdViewImpl;",
        "Lcom/applovin/impl/mediation/ads/MaxFullscreenAdImpl;",
        "Lcom/applovin/impl/mediation/ads/MaxNativeAdLoaderImpl;",
        "Lcom/applovin/mediation/ads/MaxAdView;",
        "Lcom/applovin/mediation/ads/MaxAppOpenAd;",
        "Lcom/applovin/mediation/ads/MaxInterstitialAd;",
        "Lcom/applovin/mediation/ads/MaxRewardedAd;",
        "Lcom/applovin/mediation/nativeAds/MaxNativeAdLoader;",
        "Lcom/applovin/mediation/nativeAds/MaxNativeAdView;",
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
}
