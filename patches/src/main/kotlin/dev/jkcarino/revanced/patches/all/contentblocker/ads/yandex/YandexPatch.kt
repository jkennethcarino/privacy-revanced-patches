package dev.jkcarino.revanced.patches.all.contentblocker.ads.yandex

import app.revanced.patcher.patch.BytecodePatchContext
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.booleanOption
import com.android.tools.smali.dexlib2.AccessFlags
import dev.jkcarino.revanced.util.filterMethods
import dev.jkcarino.revanced.util.findMutableMethodOf
import dev.jkcarino.revanced.util.proxy
import dev.jkcarino.revanced.util.returnEarly

internal val disableYandexOption by lazy {
    booleanOption(
        key = "disableYandex",
        default = true,
        title = "Yandex Advertising Network",
        description = "Disable Banner, Interstitial, Native, Rewarded, App Open, InStream, and Feed ad formats."
    )
}

internal fun BytecodePatchContext.applyYandexPatch() = buildList {
    val blockMethods = setOf(
        "loadAd",
        "loadAds",
        "prepareAd",
        "loadInstreamAd",
        "loadSlider",
        // Only present in YandexAdsLoader
        "requestAds",
        "setPlayer",
        "start",
        // Only present in FeedAd
        "preloadAd",
        "setLoadListener",
    )

    setOf(
        "Lcom/yandex/mobile/ads/appopenad/AppOpenAdLoader;",
        "Lcom/yandex/mobile/ads/banner/BannerAdView;",
        "Lcom/yandex/mobile/ads/feed/FeedAd;",
        "Lcom/yandex/mobile/ads/instream/InstreamAdBinder;",
        "Lcom/yandex/mobile/ads/instream/InstreamAdLoader;",
        "Lcom/yandex/mobile/ads/instream/exoplayer/YandexAdsLoader;",
        "Lcom/yandex/mobile/ads/instream/media3/YandexAdsLoader;",
        "Lcom/yandex/mobile/ads/interstitial/InterstitialAdLoader;",
        "Lcom/yandex/mobile/ads/nativeads/NativeAdLoader;",
        "Lcom/yandex/mobile/ads/nativeads/NativeBulkAdLoader;",
        "Lcom/yandex/mobile/ads/nativeads/SliderAdLoader;",
        "Lcom/yandex/mobile/ads/rewarded/RewardedAdLoader;",
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

    val inStreamInterfaces = setOf(
        "Lcom/yandex/mobile/ads/instream/inroll/Inroll;",
        "Lcom/yandex/mobile/ads/instream/player/ad/InstreamAdPlayer;",
        "Lcom/yandex/mobile/ads/instream/player/content/VideoPlayer;",
    )

    val blockInStreamMethods = setOf(
        "prepareAd",
        "playAd",
        "releaseAd",
        "resumeAd",
        "skipAd",
        "stopAd",
        "prepare",
        "play",
        "pause",
        "resume",
        "prepareVideo",
        "resumeVideo",
        "pauseVideo",
    )

    runCatching {
        classes
            .filter { classDef ->
                inStreamInterfaces.any { it in classDef.interfaces }
            }
            .filterMethods { _, method -> method.name in blockInStreamMethods }
            .ifEmpty { throw PatchException("No custom InStream implementations found") }
            .forEach { method ->
                proxy(method.definingClass)
                    .mutableClass
                    .findMutableMethodOf(method)
                    .returnEarly()
            }
    }.also(::add)

    runCatching {
        val mutableClass = loadFeedFingerprint.classDef

        mutableClass
            .filterMethods { _, method ->
                method.accessFlags == AccessFlags.PUBLIC.value or AccessFlags.FINAL.value
            }
            .forEach { method ->
                mutableClass
                    .findMutableMethodOf(method)
                    .returnEarly()
            }
    }.also(::add)
}
