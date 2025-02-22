package dev.jkcarino.revanced.patches.all.contentblocker.ads.bigo

import app.revanced.patcher.patch.BytecodePatchContext
import app.revanced.patcher.patch.booleanOption
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import dev.jkcarino.revanced.util.returnEarly
import dev.jkcarino.revanced.util.transformMethods

internal val disableBigoOption by lazy {
    booleanOption(
        key = "disableBigo",
        default = true,
        title = "BIGO",
        description = "Disable Banner, Native, Interstitial, Pop-up, Rewarded, and Splash ad formats."
    )
}

internal fun BytecodePatchContext.applyBigoPatch() {
    val splashAdClassDef = splashAdFingerprint.originalClassDef
    val blockMethods = setOf(
        "show",
        "showInAdContainer",
    )

    transformMethods(
        definingClass = splashAdClassDef.type,
        predicate = { _, method -> method.name in blockMethods },
        transform = MutableMethod::returnEarly
    )

    listOf(
        bigoAdSdkInitializeFingerprint,
        abstractAdLoaderLoadAdFingerprint,
    ).forEach { fingerprint ->
        fingerprint.method.returnEarly()
    }
}
