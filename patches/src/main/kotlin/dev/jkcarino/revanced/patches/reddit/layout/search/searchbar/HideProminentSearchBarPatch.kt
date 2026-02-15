package dev.jkcarino.revanced.patches.reddit.layout.search.searchbar

import app.revanced.patcher.patch.bytecodePatch
import dev.jkcarino.revanced.patches.reddit.misc.firebase.spoofCertificateHashPatch
import dev.jkcarino.revanced.util.returnEarly

@Suppress("unused")
val hideProminentSearchBarPatch = bytecodePatch(
    name = "Hide prominent search bar",
    description = "Hides the new experimental, prominent search bar on the main screen.",
    use = true,
) {
    dependsOn(spoofCertificateHashPatch)

    compatibleWith("com.reddit.frontpage")

    execute {
        isEnabledFingerprint.method.returnEarly()
    }
}
