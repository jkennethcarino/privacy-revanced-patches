package dev.jkcarino.revanced.patches.all.contentblocker.ads.applovin

import app.revanced.patcher.patch.BytecodePatchContext
import app.revanced.patcher.patch.booleanOption
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import dev.jkcarino.revanced.util.returnEarly
import dev.jkcarino.revanced.util.transformMethods

internal val disableAppLovinMaxOption by lazy {
    booleanOption(
        key = "disableAppLovinMax",
        default = true,
        title = "AppLovin MAX",
        description = "Disable App Open, Banner & MREC, Interstitial, Native, and Rewarded ad formats."
    )
}

internal fun BytecodePatchContext.applyAppLovinMaxPatch() {
    val appLovinSdkBlockMethods = setOf(
        "initialize",
        "initializeSdk",
        "isInitialized",
    )
    transformMethods(
        definingClass = "Lcom/applovin/sdk/AppLovinSdk;",
        predicate = { _, method -> method.name in appLovinSdkBlockMethods },
        transform = MutableMethod::returnEarly
    )

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
        transformMethods(
            definingClass = definingClass,
            predicate = { _, method -> method.name in blockMethods },
            transform = MutableMethod::returnEarly
        )
    }
}
