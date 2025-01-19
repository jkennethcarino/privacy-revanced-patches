package dev.jkcarino.revanced.patches.all.webview

import app.revanced.patcher.patch.resourcePatch
import dev.jkcarino.revanced.patches.shared.resource.androidManifest
import dev.jkcarino.revanced.patches.shared.resource.metaData

@Suppress("unused")
val disableWebViewMetricsPatch = resourcePatch(
    name = "Disable metrics collection in WebView",
    description = "Disables the collection of diagnostic data or usage statistics that are uploaded to Google.",
    use = false,
) {
    execute {
        androidManifest {
            metaData("android.webkit.WebView.MetricsOptOut" to "true")
        }
    }
}
