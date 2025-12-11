package dev.jkcarino.revanced.patches.reddit

import app.revanced.patcher.patch.bytecodePatch
import dev.jkcarino.revanced.patches.reddit.ad.removeAdsAndTelemetryPatch

@Deprecated(
    message = "This patch was moved to dev.jkcarino.revanced.patches.reddit.ad.",
    replaceWith = ReplaceWith("removeAdsAndTelemetryPatch")
)
@Suppress("unused")
val removeAdsAndTelemetry = bytecodePatch {
    dependsOn(removeAdsAndTelemetryPatch)
}
