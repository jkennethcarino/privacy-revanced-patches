package dev.jkcarino.revanced.patches.all.contentblocker.ads.vungle

import app.revanced.patcher.patch.BytecodePatchContext
import app.revanced.patcher.patch.booleanOption
import dev.jkcarino.revanced.util.returnEarly

internal val disableVungleOption by lazy {
    booleanOption(
        key = "disableVungle",
        default = true,
        title = "Liftoff Monetize",
        description = "Disable Banner, Interstitial, Native, Rewarded, and App Open ad formats."
    )
}

internal fun BytecodePatchContext.applyVunglePatch() {
    listOf(
        adInternalLoadFingerprint,
        baseFullScreenAdLoadFingerprint,
        baseAdLoadFingerprint,
        baseAdCanPlayAdFingerprint,
    ).forEach { fingerprint ->
        fingerprint.method.returnEarly()
    }
}
