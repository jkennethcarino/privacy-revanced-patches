package dev.jkcarino.revanced.patches.shared.resource

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch

abstract class BaseResourceElementRemovalPatch(
    vararg resourceRemover: ResourceRemover
) : ResourcePatch() {
    private val removers = resourceRemover.toList()

    final override fun execute(context: ResourceContext) {
        context.xmlEditor["AndroidManifest.xml"].use { editor ->
            val document = editor.file

            removers.forEach { remover ->
                remover.process(document)
            }
        }
    }
}
