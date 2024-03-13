package dev.jkcarino.revanced.patches.all.webview

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotation.Patch
import org.w3c.dom.Element

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
            val applicationTag = document
                .getElementsByTagName("application")
                .item(0) as Element
            val metaData = applicationTag.getElementsByTagName(META_DATA_TAG)

            for (i in 0 until metaData.length) {
                val element = metaData.item(i) as? Element ?: continue
                if (element.getAttribute(ATTRIBUTE_NAME) == WEBVIEW_METRICS_OPT_OUT) {
                    element.setAttribute(ATTRIBUTE_VALUE, "true")
                    return
                }
            }

            document.createElement(META_DATA_TAG).apply {
                setAttribute(ATTRIBUTE_NAME, WEBVIEW_METRICS_OPT_OUT)
                setAttribute(ATTRIBUTE_VALUE, "true")
            }.also(applicationTag::appendChild)
        }
    }
}
