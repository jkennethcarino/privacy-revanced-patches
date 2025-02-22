package dev.jkcarino.revanced.patches.all.contentblocker.ads.yandex

import app.revanced.patcher.patch.BytecodePatchContext
import app.revanced.patcher.patch.booleanOption
import dev.jkcarino.revanced.util.returnEarly

internal val disableYandexOption by lazy {
    booleanOption(
        key = "disableYandex",
        default = true,
        title = "Yandex Advertising Network",
        description = "Disable Banner, Interstitial, Native, Rewarded, App Open, and InStream ad formats."
    )
}

internal fun BytecodePatchContext.applyYandexPatch() {
    listOf(
        appOpenLoaderLoadFingerprint,
        bannerAdViewLoadAdFingerprint,
        inStreamAdBinderPrepareAdFingerprint,
        inStreamAdLoaderLoadAdFingerprint,
        interstitialAdLoaderLoadAdFingerprint,
        nativeAdLoaderLoadAdFingerprint,
        nativeBulkAdLoaderLoadAdsFingerprint,
        rewardedAdLoaderLoadFingerprint,
        sliderAdLoaderLoadSliderFingerprint,
    ).forEach { fingerprint ->
        fingerprint.method.returnEarly()
    }
}
