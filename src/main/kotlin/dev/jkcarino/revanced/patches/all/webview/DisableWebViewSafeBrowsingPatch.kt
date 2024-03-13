package dev.jkcarino.revanced.patches.all.webview

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotation.Patch
import org.w3c.dom.Element

@Patch(
    name = "Disable Google Safe Browsing in WebView",
    description = "Disables the Google Safe Browsing checks in WebView. This doesn't apply to WebView within the SDK Runtime.",
    use = false
)
@Suppress("unused")
object DisableWebViewSafeBrowsingPatch : ResourcePatch() {
    private const val META_DATA_TAG = "meta-data"
    private const val ATTRIBUTE_NAME = "android:name"
    private const val ATTRIBUTE_VALUE = "android:value"
    private const val WEBVIEW_SAFE_BROWSING = "android.webkit.WebView.EnableSafeBrowsing"

    override fun execute(context: ResourceContext) {
        context.xmlEditor["AndroidManifest.xml"].use { editor ->
            val document = editor.file
            val applicationTag = document
                .getElementsByTagName("application")
                .item(0) as Element
            val metaData = applicationTag.getElementsByTagName(META_DATA_TAG)

            for (i in 0 until metaData.length) {
                val element = metaData.item(i) as? Element ?: continue
                if (element.getAttribute(ATTRIBUTE_NAME) == WEBVIEW_SAFE_BROWSING) {
                    element.setAttribute(ATTRIBUTE_VALUE, "false")
                    return
                }
            }

            document.createElement(META_DATA_TAG).apply {
                setAttribute(ATTRIBUTE_NAME, WEBVIEW_SAFE_BROWSING)
                setAttribute(ATTRIBUTE_VALUE, "false")
            }.also(applicationTag::appendChild)
        }
    }
}
