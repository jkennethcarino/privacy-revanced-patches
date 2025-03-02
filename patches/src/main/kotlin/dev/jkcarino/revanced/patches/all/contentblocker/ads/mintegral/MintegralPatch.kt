package dev.jkcarino.revanced.patches.all.contentblocker.ads.mintegral

import app.revanced.patcher.patch.BytecodePatchContext
import app.revanced.patcher.patch.booleanOption
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import dev.jkcarino.revanced.util.returnEarly
import dev.jkcarino.revanced.util.transformMethods

internal val disableMintegralOption by lazy {
    booleanOption(
        key = "disableMintegral",
        default = true,
        title = "Mintegral",
        description = "Disable Banner, Interstitial, Rewarded Video, Native, and Splash ad formats."
    )
}

internal fun BytecodePatchContext.applyMintegralPatch() {
    val blockMethods = setOf(
        "init",
        "load",
        "loadFromBid",
        "isReady",
        // Only present in MBBidInterstitialVideoHandler, MBInterstitialVideoHandler, and MBNewInterstitialHandler
        "isBidReady",
        "loadFormSelfFilling",
        "showFromBid",
        // Only present in MBRewardVideoHandler
        "show",
        // Only present in MBSplashView
        "isImageReady",
        "isVideoReady",
        // Only present in MBNativeAdvancedHandler
        "loadByToken",
    )

    setOf(
        "Lcom/mbridge/msdk/interstitialvideo/out/MBBidInterstitialVideoHandler;",
        "Lcom/mbridge/msdk/interstitialvideo/out/MBInterstitialVideoHandler;",
        "Lcom/mbridge/msdk/newinterstitial/out/MBNewInterstitialHandler",
        "Lcom/mbridge/msdk/newout/MBBidRewardVideoHandler;",
        "Lcom/mbridge/msdk/newout/MBRewardVideoHandler;",
        "Lcom/mbridge/msdk/out/MBBannerView;",
        "Lcom/mbridge/msdk/out/MBNativeAdvancedHandler;",
        "Lcom/mbridge/msdk/out/MBSplashHandler;",
        "Lcom/mbridge/msdk/splash/view/MBSplashView;",
    ).forEach { definingClass ->
        transformMethods(
            definingClass = definingClass,
            predicate = { _, method -> method.name in blockMethods },
            transform = MutableMethod::returnEarly
        )
    }

    mBridgeSdkInitFingerprint.method.returnEarly()
}
