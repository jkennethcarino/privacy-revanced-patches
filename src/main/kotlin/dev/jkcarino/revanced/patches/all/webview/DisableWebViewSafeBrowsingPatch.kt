package dev.jkcarino.revanced.patches.all.webview

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotation.Patch
import dev.jkcarino.revanced.util.createElement
import dev.jkcarino.revanced.util.filterToElements
import dev.jkcarino.revanced.util.firstElementByTagName

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
            val application = document.firstElementByTagName("application")

            val webViewSafeBrowsingMetaData = application.getElementsByTagName(META_DATA_TAG)
                .filterToElements()
                .firstOrNull { it.getAttribute(ATTRIBUTE_NAME) == WEBVIEW_SAFE_BROWSING }
                ?.setAttribute(ATTRIBUTE_VALUE, "false")

            if (webViewSafeBrowsingMetaData == null) {
                application.appendChild(
                    document.createElement(META_DATA_TAG) {
                        setAttribute(ATTRIBUTE_NAME, WEBVIEW_SAFE_BROWSING)
                        setAttribute(ATTRIBUTE_VALUE, "false")
                    }
                )
            }
        }
    }
}
