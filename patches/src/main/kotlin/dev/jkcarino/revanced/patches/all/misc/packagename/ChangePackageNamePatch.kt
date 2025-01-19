package dev.jkcarino.revanced.patches.all.misc.packagename

import app.revanced.patcher.patch.resourcePatch
import app.revanced.patcher.patch.stringOption
import dev.jkcarino.revanced.patches.shared.resource.MANIFEST_NODE
import dev.jkcarino.revanced.patches.shared.resource.androidManifest
import dev.jkcarino.revanced.util.asAttributeSequence
import dev.jkcarino.revanced.util.asElementSequence
import dev.jkcarino.revanced.util.get
import dev.jkcarino.revanced.util.set
import org.w3c.dom.NodeList

@Suppress("unused")
val changePackageNamePatch = resourcePatch(
    name = "Change package name",
    description = "Appends \".revanced\" to the package name by default. " +
        "Changing the package name of the app can lead to unexpected issues.",
    use = false,
) {
    val packageNameOption =
        stringOption(
            key = "packageName",
            default = "Default",
            values = mapOf("Default" to "Default"),
            title = "Package name",
            description = "The name of the package to rename the app to.",
            required = true,
        ) {
            it == "Default" || it!!.matches(Regex("^[a-z]\\w*(\\.[a-z]\\w*)+\$"))
        }

    finalize {
        fun NodeList.searchAndReplace(old: String, new: String) {
            asElementSequence()
                .flatMap { it.attributes.asAttributeSequence() }
                .filter { it.nodeValue.startsWith(old) }
                .forEach { it.nodeValue = it.nodeValue.replace(old, new) }
        }

        androidManifest {
            val manifest = this[MANIFEST_NODE]
            val packageName = manifest["package"]
            val replacementPackageName = packageNameOption.value!!
            val newPackageName = if (replacementPackageName != packageNameOption.default) {
                replacementPackageName
            } else {
                "${packageName}.revanced"
            }

            manifest["package"] = newPackageName

            // We must replace the package name in all <provider> elements, as the
            // installation may fail if the provider name is already in use by another package.
            setOf("provider", "permission", "uses-permission")
                .map(::getElementsByTagName)
                .forEach { it.searchAndReplace(packageName, newPackageName) }
        }
    }
}
