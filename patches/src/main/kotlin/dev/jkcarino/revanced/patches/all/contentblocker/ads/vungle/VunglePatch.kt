package dev.jkcarino.revanced.patches.all.contentblocker.ads.vungle

import app.revanced.patcher.patch.BytecodePatchContext
import app.revanced.patcher.patch.booleanOption
import dev.jkcarino.revanced.util.filterMethods
import dev.jkcarino.revanced.util.findMutableMethodOf
import dev.jkcarino.revanced.util.proxy
import dev.jkcarino.revanced.util.returnEarly

internal val disableVungleOption = booleanOption(
    key = "disableVungle",
    default = true,
    title = "Liftoff Monetize",
    description = "Disable Banner, Interstitial, Native, Rewarded, and App Open ad formats."
)

internal fun BytecodePatchContext.applyVunglePatch() = buildList {
    val blockMethods = setOf(
        "load",
        "loadAd",
        "canPlayAd",
    )

    setOf(
        "Lcom/vungle/ads/internal/AdInternal;",
        "Lcom/vungle/ads/BaseFullscreenAd;",
        "Lcom/vungle/ads/BaseAd;",
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
}
