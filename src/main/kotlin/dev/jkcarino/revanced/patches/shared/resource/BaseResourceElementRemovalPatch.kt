package dev.jkcarino.revanced.patches.shared.resource

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch

abstract class BaseResourceElementRemovalPatch(
    vararg elementProcessor: ElementProcessor
) : ResourcePatch() {
    private val processors = elementProcessor.toList()

    final override fun execute(context: ResourceContext) {
        context.xmlEditor["AndroidManifest.xml"].use { editor ->
            val document = editor.file

            processors.forEach { processor ->
                processor.process(document)
            }
        }
    }
}
