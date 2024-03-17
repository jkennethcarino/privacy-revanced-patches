package dev.jkcarino.revanced.patches.all.webview

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotation.Patch
import dev.jkcarino.revanced.util.createElement
import dev.jkcarino.revanced.util.filterToElements
import dev.jkcarino.revanced.util.firstElementByTagName

@Patch(
    name = "Disable metrics collection in WebView",
    description = "Disables the collection of diagnostic data or usage statistics that are uploaded to Google.",
    use = false
)
@Suppress("unused")
object DisableWebViewMetricsPatch : ResourcePatch() {
    private const val META_DATA_TAG = "meta-data"
    private const val ATTRIBUTE_NAME = "android:name"
    private const val ATTRIBUTE_VALUE = "android:value"
    private const val WEBVIEW_METRICS_OPT_OUT = "android.webkit.WebView.MetricsOptOut"

    override fun execute(context: ResourceContext) {
        context.xmlEditor["AndroidManifest.xml"].use { editor ->
            val document = editor.file
            val application = document.firstElementByTagName("application")

            val webViewMetricsOptOutMetaData = application.getElementsByTagName(META_DATA_TAG)
                .filterToElements()
                .firstOrNull { it.getAttribute(ATTRIBUTE_NAME) == WEBVIEW_METRICS_OPT_OUT }
                ?.setAttribute(ATTRIBUTE_VALUE, "true")

            if (webViewMetricsOptOutMetaData == null) {
                application.appendChild(
                    document.createElement(META_DATA_TAG) {
                        setAttribute(ATTRIBUTE_NAME, WEBVIEW_METRICS_OPT_OUT)
                        setAttribute(ATTRIBUTE_VALUE, "true")
                    }
                )
            }
        }
    }
}
