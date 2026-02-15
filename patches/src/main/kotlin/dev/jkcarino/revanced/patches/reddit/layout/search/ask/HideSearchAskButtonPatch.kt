package dev.jkcarino.revanced.patches.reddit.layout.search.ask

import app.revanced.patcher.patch.bytecodePatch
import dev.jkcarino.revanced.patches.reddit.misc.firebase.spoofCertificateHashPatch
import dev.jkcarino.revanced.util.returnEarly

@Suppress("unused")
val hideSearchAskButtonPatch = bytecodePatch(
    name = "Hide Ask button from search bar",
    description = "Hides the experimental Ask button (Reddit Answers) from the search bar.",
    use = false,
) {
    dependsOn(spoofCertificateHashPatch)

    compatibleWith("com.reddit.frontpage")

    execute {
        isSearchBarAskButtonHoldoutEnabledFingerprint
            .match(staticConstructorFingerprint.classDef)
            .method
            .returnEarly()
    }
}
