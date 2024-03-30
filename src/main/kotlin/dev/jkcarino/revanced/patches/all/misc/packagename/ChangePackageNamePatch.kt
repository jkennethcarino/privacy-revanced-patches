package dev.jkcarino.revanced.patches.all.misc.packagename

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.patch.options.PatchOption.PatchExtensions.stringPatchOption
import dev.jkcarino.revanced.util.asAttributeSequence
import dev.jkcarino.revanced.util.asElementSequence
import dev.jkcarino.revanced.util.firstElementByTagName
import org.w3c.dom.NodeList
import java.io.Closeable

@Patch(
    name = "Change package name",
    description = "Appends \".revanced\" to the package name by default. Changing the package name of the app can lead to unexpected issues.",
    use = false,
)
@Suppress("unused")
object ChangePackageNamePatch : ResourcePatch(), Closeable {
    private val packageNameOption =
        stringPatchOption(
            key = "packageName",
            default = "Default",
            values = mapOf("Default" to "Default"),
            title = "Package name",
            description = "The name of the package to rename the app to.",
            required = true,
        ) {
            it == "Default" || it!!.matches(Regex("^[a-z]\\w*(\\.[a-z]\\w*)+\$"))
        }

    private lateinit var context: ResourceContext

    override fun execute(context: ResourceContext) {
        this.context = context
    }

    override fun close() {
        fun NodeList.searchAndReplace(old: String, new: String) {
            asElementSequence()
                .flatMap { it.attributes.asAttributeSequence() }
                .filter { it.nodeValue.startsWith(old) }
                .forEach { it.nodeValue = it.nodeValue.replace(old, new) }
        }

        context.xmlEditor["AndroidManifest.xml"].use { editor ->
            val document = editor.file
            val manifest = document.firstElementByTagName("manifest")

            val replacementPackageName = packageNameOption.value!!
            val currentPackageName = manifest.getAttribute("package")
            val newPackageName = if (replacementPackageName != packageNameOption.default) {
                replacementPackageName
            } else {
                "${currentPackageName}.revanced"
            }

            manifest.setAttribute("package", newPackageName)

            // We must replace the package name in all <provider> elements, as the
            // installation may fail if the provider name is already in use by another package.
            document.getElementsByTagName("provider")
                .searchAndReplace(currentPackageName, newPackageName)

            document.getElementsByTagName("permission")
                .searchAndReplace(currentPackageName, newPackageName)

            document.getElementsByTagName("uses-permission")
                .searchAndReplace(currentPackageName, newPackageName)
        }
    }
}
