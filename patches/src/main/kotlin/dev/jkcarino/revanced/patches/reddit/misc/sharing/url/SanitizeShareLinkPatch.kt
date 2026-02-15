package dev.jkcarino.revanced.patches.reddit.misc.sharing.url

import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.patch.bytecodePatch
import dev.jkcarino.revanced.patches.reddit.misc.firebase.spoofCertificateHashPatch

@Suppress("unused")
val sanitizeShareLinkPatch = bytecodePatch(
    name = "Sanitize share links",
    description = "Unshortens and removes the tracking query parameters from shared links.",
    use = true,
) {
    dependsOn(spoofCertificateHashPatch)

    compatibleWith("com.reddit.frontpage")

    execute {
        createShareLinkFingerprint.method.addInstruction(
            index = 0,
            smaliInstructions = "return-object p0"
        )

        val generateShareLinkMethod = generateShareLinkFingerprint.method
        val getShortUrlMethod = navigate(generateShareLinkMethod)
            .to(generateShareLinkFingerprint.patternMatch!!.endIndex - 1)
            .stop()

        getShortUrlMethod.addInstruction(
            index = 0,
            smaliInstructions = "return-object p1"
        )
    }
}
