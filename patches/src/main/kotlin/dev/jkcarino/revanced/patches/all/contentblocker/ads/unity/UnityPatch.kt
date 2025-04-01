package dev.jkcarino.revanced.patches.all.contentblocker.ads.unity

import app.revanced.patcher.patch.BytecodePatchContext
import app.revanced.patcher.patch.booleanOption
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import dev.jkcarino.revanced.util.returnEarly
import dev.jkcarino.revanced.util.transformMethods

internal val disableUnityOption by lazy {
    booleanOption(
        key = "disableUnity",
        default = true,
        title = "Unity",
        description = "Disable Banner, Interstitial, Native, and Rewarded Video ad formats."
    )
}

internal fun BytecodePatchContext.applyUnityPatch() {
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
        transformMethods(
            definingClass = definingClass,
            predicate = { _, method -> method.name in blockMethods },
            transform = MutableMethod::returnEarly
        )
    }

    unityServicesInitializeFingerprint.method.returnEarly()
}
