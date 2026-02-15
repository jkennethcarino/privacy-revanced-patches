package dev.jkcarino.revanced.patches.all.firebase.installations

import app.revanced.patcher.fingerprint

/**
 * See: https://github.com/firebase/firebase-android-sdk/blob/c8ada3ce645798bd8bacd5c9b5cb08bdf7254a34/firebase-installations/src/main/java/com/google/firebase/installations/remote/FirebaseInstallationServiceClient.java#L495
 */
internal val openHttpUrlConnectionFingerprint = fingerprint {
    returns("Ljava/net/HttpURLConnection;")
    parameters(
        "Ljava/net/URL;",
        "Ljava/lang/String;",
    )
    strings(
        "X-Android-Cert",
        "Firebase Installations Service is unavailable. Please try again later.",
    )
}
