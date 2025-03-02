package dev.jkcarino.revanced.patches.all.contentblocker.ads.yandex

import app.revanced.patcher.fingerprint
import com.android.tools.smali.dexlib2.AccessFlags

internal val appOpenLoaderLoadFingerprint = fingerprint {
    accessFlags(AccessFlags.PUBLIC, AccessFlags.FINAL)
    returns("V")
    parameters("L")
    custom { method, _ ->
        method.definingClass == "Lcom/yandex/mobile/ads/appopenad/AppOpenAdLoader;" &&
            method.name == "loadAd"
    }
}

internal val bannerAdViewLoadAdFingerprint = fingerprint {
    accessFlags(AccessFlags.PUBLIC, AccessFlags.FINAL)
    returns("V")
    parameters("L")
    custom { method, _ ->
        method.definingClass == "Lcom/yandex/mobile/ads/banner/BannerAdView;" &&
            method.name == "loadAd"
    }
}

internal val inStreamAdBinderPrepareAdFingerprint = fingerprint {
    accessFlags(AccessFlags.PUBLIC, AccessFlags.FINAL)
    returns("V")
    parameters()
    custom { method, _ ->
        method.definingClass == "Lcom/yandex/mobile/ads/instream/InstreamAdBinder;" &&
            method.name == "prepareAd"
    }
}

internal val inStreamAdLoaderLoadAdFingerprint = fingerprint {
    accessFlags(AccessFlags.PUBLIC, AccessFlags.FINAL)
    returns("V")
    parameters("Landroid/content/Context;", "L")
    custom { method, _ ->
        method.definingClass == "Lcom/yandex/mobile/ads/instream/InstreamAdLoader;" &&
            method.name == "loadInstreamAd"
    }
}

internal val interstitialAdLoaderLoadAdFingerprint = fingerprint {
    accessFlags(AccessFlags.PUBLIC, AccessFlags.FINAL)
    returns("V")
    parameters("L")
    custom { method, _ ->
        method.definingClass == "Lcom/yandex/mobile/ads/interstitial/InterstitialAdLoader;" &&
            method.name == "loadAd"
    }
}

internal val nativeAdLoaderLoadAdFingerprint = fingerprint {
    accessFlags(AccessFlags.PUBLIC, AccessFlags.FINAL)
    returns("V")
    parameters("L")
    custom { method, _ ->
        method.definingClass == "Lcom/yandex/mobile/ads/nativeads/NativeAdLoader;" &&
            method.name == "loadAd"
    }
}

internal val nativeBulkAdLoaderLoadAdsFingerprint = fingerprint {
    accessFlags(AccessFlags.PUBLIC, AccessFlags.FINAL)
    returns("V")
    parameters("L", "I")
    custom { method, _ ->
        method.definingClass == "Lcom/yandex/mobile/ads/nativeads/NativeBulkAdLoader;" &&
            method.name == "loadAds"
    }
}

internal val rewardedAdLoaderLoadFingerprint = fingerprint {
    accessFlags(AccessFlags.PUBLIC, AccessFlags.FINAL)
    returns("V")
    parameters("L")
    custom { method, _ ->
        method.definingClass == "Lcom/yandex/mobile/ads/rewarded/RewardedAdLoader;" &&
            method.name == "loadAd"
    }
}

internal val sliderAdLoaderLoadSliderFingerprint = fingerprint {
    accessFlags(AccessFlags.PUBLIC, AccessFlags.FINAL)
    returns("V")
    parameters("L")
    custom { method, _ ->
        method.definingClass == "Lcom/yandex/mobile/ads/nativeads/SliderAdLoader;" &&
            method.name == "loadSlider"
    }
}
