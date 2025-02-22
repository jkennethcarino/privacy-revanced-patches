package dev.jkcarino.revanced.patches.all.contentblocker.ads.topon

import app.revanced.patcher.patch.BytecodePatchContext
import app.revanced.patcher.patch.booleanOption
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import dev.jkcarino.revanced.util.returnEarly
import dev.jkcarino.revanced.util.transformMethods

internal val disableTopOnOption by lazy {
    booleanOption(
        key = "disableTopOn",
        default = true,
        title = "TopOn",
        description = "Disable Banner, Interstitial, Native, Rewarded, and App Open ad formats."
    )
}

internal fun BytecodePatchContext.applyTopOnPatch() {
    val blockMethods = setOf(
        "entryAdScenario",
        "load",
        "isAdReady",
        "controlShow",
        "show",
        // Only present in Interstitial, SplashAd, and customized BannerView
        "setNativeAdCustomRender",
        // Only present in BannerView and SplashAd
        "loadAd",
        // Only present in NativeAd
        "getNativeAd",
        "makeAdRequest",
        "renderAdContainer",
        "prepare",
        // Only present in InStream
        "setOnIMAEventListener",
    )

    setOf(
        "Lcom/anythink/banner/api/ATBannerView;",
        "Lcom/anythink/interstitial/api/ATInterstitial;",
        "Lcom/anythink/interstitial/api/ATInterstitialAutoAd;",
        "Lcom/anythink/nativead/api/ATNative;",
        "Lcom/anythink/rewardvideo/api/ATRewardVideoAd;",
        "Lcom/anythink/rewardvideo/api/ATRewardVideoAutoAd;",
        "Lcom/anythink/splashad/api/ATSplashAd;",
    ).forEach { definingClass ->
        transformMethods(
            definingClass = definingClass,
            predicate = { _, method -> method.name in blockMethods },
            transform = MutableMethod::returnEarly
        )
    }

    listOf(
        atSdkInitFingerprint,
        atRewardedVideoAdLoadManagerShowFingerprint,
    ).forEach { fingerprint ->
        fingerprint.method.returnEarly()
    }
}
