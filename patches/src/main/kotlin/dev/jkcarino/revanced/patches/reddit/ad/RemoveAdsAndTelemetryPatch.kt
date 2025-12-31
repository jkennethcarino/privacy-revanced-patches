package dev.jkcarino.revanced.patches.reddit.ad

import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import dev.jkcarino.revanced.util.proxy

private const val EXTENSION_CLASS_DESCRIPTOR =
    "Ldev/jkcarino/extension/reddit/frontpage/AdBlockInterceptor;"

@Suppress("unused")
val removeAdsAndTelemetryPatch = bytecodePatch(
    name = "Remove ads and telemetry",
    description = "Removes ads and telemetry from Home, Popular, Watch, Latest, All, " +
        "Custom feeds, Search, and Subreddits, including comments.",
    use = true,
) {
    extendWith("extensions/reddit/frontpage.rve")

    compatibleWith("com.reddit.frontpage")

    execute {
        okHttpConstructorFingerprint.method.apply {
            val interceptorsIndex = okHttpConstructorFingerprint.patternMatch!!.endIndex
            val interceptorsInstruction = getInstruction<OneRegisterInstruction>(interceptorsIndex)
            val interceptorsRegister = interceptorsInstruction.registerA
            val adBlockInterceptorRegister = interceptorsRegister + 1

            addInstructions(
                index = interceptorsIndex + 1,
                smaliInstructions = """
                    invoke-static {}, $EXTENSION_CLASS_DESCRIPTOR->getInstance()$EXTENSION_CLASS_DESCRIPTOR
                    move-result-object v$adBlockInterceptorRegister
                    invoke-virtual {v$adBlockInterceptorRegister, v$interceptorsRegister}, $EXTENSION_CLASS_DESCRIPTOR->inject(Ljava/util/List;)V
                """
            )
        }

        val adBlockInterceptorClass = proxy(EXTENSION_CLASS_DESCRIPTOR).immutableClass

        interceptFingerprint.match(adBlockInterceptorClass).method.apply {
            val realBufferedSourceClassDef =
                realBufferedSourceCommonIndexOfFingerprint.originalClassDef
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

            mapOf(
                responseBodySourceIndex to
                    "invoke-virtual {v0}, Lokhttp3/ResponseBody;->source()$bufferedSource",
                sourceRequestIndex to
                    "invoke-interface {v0, v2, v3}, $bufferedSource->request(J)Z",
                sourceGetBufferIndex to
                    "invoke-interface {v0}, $bufferedSource->$getBuffer()$buffer",
                bufferCloneIndex to
                    "invoke-virtual {v0}, $buffer->$realClone()$buffer",
                bufferReadStringIndex to
                    "invoke-virtual {v0, v2}, $buffer->$readString(Ljava/nio/charset/Charset;)Ljava/lang/String;"
            ).forEach { (index, smali) ->
                replaceInstruction(index, smali)
            }
        }
    }
}
