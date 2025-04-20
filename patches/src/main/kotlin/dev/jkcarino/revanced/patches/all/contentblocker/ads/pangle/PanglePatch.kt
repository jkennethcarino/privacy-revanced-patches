package dev.jkcarino.revanced.patches.all.contentblocker.ads.pangle

import app.revanced.patcher.patch.BytecodePatchContext
import app.revanced.patcher.patch.booleanOption
import dev.jkcarino.revanced.util.returnEarly

internal val disablePangleOption = booleanOption(
    key = "disablePangle",
    default = true,
    title = "Pangle",
    description = "Disable Banner, Interstitial, Native, Rewarded Video, and App Open ad formats."
)

internal fun BytecodePatchContext.applyPanglePatch() = buildList {
    runCatching {
        sdkLoadAdFactoryFingerprint.method.returnEarly()
    }.also(::add)
}
