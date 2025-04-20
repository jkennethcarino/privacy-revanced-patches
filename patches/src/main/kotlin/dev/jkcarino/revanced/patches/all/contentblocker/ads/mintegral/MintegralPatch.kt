package dev.jkcarino.revanced.patches.all.contentblocker.ads.mintegral

import app.revanced.patcher.patch.BytecodePatchContext
import app.revanced.patcher.patch.booleanOption
import dev.jkcarino.revanced.util.filterMethods
import dev.jkcarino.revanced.util.findMutableMethodOf
import dev.jkcarino.revanced.util.proxy
import dev.jkcarino.revanced.util.returnEarly

internal val disableMintegralOption = booleanOption(
    key = "disableMintegral",
    default = true,
    title = "Mintegral",
    description = "Disable Banner, Interstitial, Rewarded Video, Native, and Splash ad formats."
)

internal fun BytecodePatchContext.applyMintegralPatch() = buildList {
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
        mBridgeSdkInitFingerprint.method.returnEarly()
    }.also(::add)
}
