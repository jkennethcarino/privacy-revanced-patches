package dev.jkcarino.revanced.patches.reddit

import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.patch.bytecodePatch
import dev.jkcarino.revanced.util.proxy

private const val EXTENSION_CLASS_DESCRIPTOR = "Ldev/jkcarino/extension/reddit/AdBlockInterceptor;"

@Suppress("unused")
val removeAdsAndTelemetry = bytecodePatch(
    name = "Remove ads, annoyances, and telemetry",
    description = "Removes ads, annoyances, and telemetry from Home, Popular, Watch, Latest, All, " +
        "Custom feeds, Search, and Subreddits, including comments.",
    use = false
) {
    extendWith("extensions/reddit.rve")

    compatibleWith("com.reddit.frontpage")

    execute {
        okHttpConstructorFingerprint.method.apply {
            val interceptorsIndex = okHttpConstructorFingerprint.patternMatch!!.endIndex + 1

            addInstructions(
                index = interceptorsIndex,
                smaliInstructions = """
                    invoke-static {}, $EXTENSION_CLASS_DESCRIPTOR->getInstance()$EXTENSION_CLASS_DESCRIPTOR
                    move-result-object v3
                    invoke-virtual {v3, v2}, $EXTENSION_CLASS_DESCRIPTOR->inject(Ljava/util/List;)V
                """
            )
        }

        val adBlockInterceptorClass = proxy(EXTENSION_CLASS_DESCRIPTOR).immutableClass

        interceptFingerprint.match(adBlockInterceptorClass).method.apply {
            val realBufferedSourceClassDef = realBufferedSourceCommonIndexOfFingerprint.originalClassDef
            val bufferedSource = realBufferedSourceClassDef.interfaces.first()
            val bufferClassDef = bufferCommonReadAndWriteUnsafeFingerprint.originalClassDef
            val buffer = bufferClassDef.type

            val getBuffer = bufferedSourceGetBufferFingerprint(bufferClassDef)
                .match(realBufferedSourceClassDef)
                .method
                .name

            val cloneMethod = bufferCloneFingerprint.match(bufferClassDef).method
            val realClone = navigate(cloneMethod).to(0).original().name

            val readString = bufferReadStringFingerprint
                .match(bufferClassDef)
                .method
                .name

            val responseBodySourceIndex = interceptFingerprint.patternMatch!!.startIndex
            val sourceRequestIndex = responseBodySourceIndex + 3
            val sourceGetBufferIndex = sourceRequestIndex + 1
            val bufferCloneIndex = sourceGetBufferIndex + 2
            val bufferReadStringIndex = interceptFingerprint.patternMatch!!.endIndex

            replaceInstruction(
                index = responseBodySourceIndex,
                smaliInstruction = """
                    invoke-virtual {v0}, Lokhttp3/ResponseBody;->source()$bufferedSource
                """
            )
            replaceInstruction(
                index = sourceRequestIndex,
                smaliInstruction = """
                    invoke-interface {v0, v2, v3}, $bufferedSource->request(J)Z
                """
            )
            replaceInstruction(
                index = sourceGetBufferIndex,
                smaliInstruction = """
                    invoke-interface {v0}, $bufferedSource->$getBuffer()$buffer
                """
            )
            replaceInstruction(
                index = bufferCloneIndex,
                smaliInstruction = """
                    invoke-virtual {v0}, $buffer->$realClone()$buffer
                """
            )
            replaceInstruction(
                index = bufferReadStringIndex,
                smaliInstruction = """
                    invoke-virtual {v0, v2}, $buffer->$readString(Ljava/nio/charset/Charset;)Ljava/lang/String;
                """
            )
        }
    }
}
