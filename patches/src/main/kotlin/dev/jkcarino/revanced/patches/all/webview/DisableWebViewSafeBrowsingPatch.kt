package dev.jkcarino.revanced.patches.all.webview

import app.revanced.patcher.patch.resourcePatch
import dev.jkcarino.revanced.patches.shared.resource.androidManifest
import dev.jkcarino.revanced.patches.shared.resource.metaData

@Suppress("unused")
val disableWebViewSafeBrowsingPatch = resourcePatch(
    name = "Disable Google Safe Browsing in WebView",
    description = "Disables the Google Safe Browsing checks in WebView. This doesn't apply to WebView within the SDK Runtime.",
    use = false,
) {
    execute {
        androidManifest {
            metaData("android.webkit.WebView.EnableSafeBrowsing" to "false")
        }
    }
}
