package dev.jkcarino.revanced.patches.reddit.misc.outboundlink

import app.revanced.patcher.patch.bytecodePatch
import dev.jkcarino.revanced.patches.reddit.shared.linkToStringFingerprint
import dev.jkcarino.revanced.util.returnEarly

@Suppress("unused")
val openLinksDirectlyPatch = bytecodePatch(
    name = "Open external links directly",
    description = "Opens external links directly without going through out.reddit.com.",
    use = true,
) {
    compatibleWith("com.reddit.frontpage")

    execute {
        mapOf(
            accountPreferencesToStringFingerprint to getAllowClickTrackingFingerprint,
            accountToStringFingerprint to getOutboundClickTrackingFingerprint,
            linkToStringFingerprint to getOutboundLinkFingerprint,
        ).forEach { (toStringFingerprint, targetFingerprint) ->
            targetFingerprint
                .match(toStringFingerprint.classDef)
                .method
                .returnEarly()
        }
    }
}
