package dev.jkcarino.revanced.patches.all.contentblocker.ads.bigo

import app.revanced.patcher.patch.BytecodePatchContext
import app.revanced.patcher.patch.booleanOption
import dev.jkcarino.revanced.util.filterMethods
import dev.jkcarino.revanced.util.findMutableMethodOf
import dev.jkcarino.revanced.util.returnEarly

internal val disableBigoOption by lazy {
    booleanOption(
        key = "disableBigo",
        default = true,
        title = "BIGO",
        description = "Disable Banner, Native, Interstitial, Pop-up, Rewarded, and Splash ad formats."
    )
}

internal fun BytecodePatchContext.applyBigoPatch() = buildList {
    val blockMethods = setOf(
        "show",
        "showInAdContainer",
    )
    runCatching {
        val mutableClass = splashAdFingerprint.classDef

        mutableClass
            .filterMethods { _, method -> method.name in blockMethods }
            .forEach { method ->
                mutableClass
                    .findMutableMethodOf(method)
                    .returnEarly()
            }
    }.also(::add)

    listOf(
        bigoAdSdkInitializeFingerprint,
        abstractAdLoaderLoadAdFingerprint,
    ).forEach { fingerprint ->
        runCatching {
            fingerprint.method.returnEarly()
        }.also(::add)
    }
}
