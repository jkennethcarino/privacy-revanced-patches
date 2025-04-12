package dev.jkcarino.revanced.patches.all.contentblocker.ads.unity

import app.revanced.patcher.patch.BytecodePatchContext
import app.revanced.patcher.patch.booleanOption
import dev.jkcarino.revanced.util.filterMethods
import dev.jkcarino.revanced.util.findMutableMethodOf
import dev.jkcarino.revanced.util.proxy
import dev.jkcarino.revanced.util.returnEarly

internal val disableUnityOption by lazy {
    booleanOption(
        key = "disableUnity",
        default = true,
        title = "Unity",
        description = "Disable Banner, Interstitial, Native, and Rewarded Video ad formats."
    )
}

internal fun BytecodePatchContext.applyUnityPatch() = buildList {
    val unityAdsClassDef = unityAdsIsInitializedFingerprint.originalClassDefOrNull
    val blockMethods = setOf(
        "initialize",
        "isInitialized",
        "isSupported",
        "load",
        "show",
        // Only present in BannerView
        "loadWebPlayer",
    )

    setOfNotNull(
        unityAdsClassDef?.type,
        "Lcom/unity3d/services/banners/BannerView;",
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
        unityServicesInitializeFingerprint.method.returnEarly()
    }.also(::add)
}
