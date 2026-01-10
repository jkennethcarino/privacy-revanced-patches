package dev.jkcarino.revanced.patches.reddit.misc.firebase

import app.revanced.patcher.patch.bytecodePatch
import dev.jkcarino.revanced.patches.all.firebase.installations.baseSpoofAndroidCertPatch

@Suppress("unused")
val spoofCertificateHashPatch = bytecodePatch(
    description = "Spoofs the app's Firebase certificate hash to allow push notifications.",
) {
    dependsOn(
        baseSpoofAndroidCertPatch(
            certificateHash = { "8BCA1DDA8B2418E5300FC91AC43BBF211290792E" }
        )
    )

    compatibleWith("com.reddit.frontpage")
}
