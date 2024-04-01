package dev.jkcarino.revanced.patches.all.signature.pmshook

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.extensions.or
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod.Companion.toMutable
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.immutable.ImmutableMethod
import dev.jkcarino.revanced.patches.all.signature.pmshook.fingerprints.AttachBaseContextFingerprint
import dev.jkcarino.revanced.patches.all.signature.pmshook.fingerprints.ConstructorFingerprint
import dev.jkcarino.revanced.util.transformMethods
import dev.jkcarino.revanced.util.traverseClassHierarchy

@Patch(
    name = "Bypass signature verification checks",
    description = "Bypasses the signature verification checks when the app starts up. This requires the original app to work properly.",
    dependencies = [
        ReplaceSubApplicationPatch::class,
        EncodeCertificatePatch::class,
    ],
    use = false,
    requiresIntegrations = true
)
object BypassSignatureChecksPatch : BytecodePatch(
    setOf(
        ConstructorFingerprint,
        AttachBaseContextFingerprint,
    )
) {
    override fun execute(context: BytecodeContext) {
        val originalSubApplicationClass = ReplaceSubApplicationPatch.subApplicationClass
        val signature = EncodeCertificatePatch.signature

        val fingerprintResult = AttachBaseContextFingerprint.result!!
        val signatureIndex = fingerprintResult.scanResult.patternScanResult!!.startIndex
        fingerprintResult.mutableMethod.replaceInstruction(
            index = signatureIndex,
            smaliInstruction = "const-string v1, \"$signature\""
        )

        if (originalSubApplicationClass.isNotEmpty()) {
            val className = originalSubApplicationClass.replace(".", "/")
            val classDescriptor = "L$className;"
            val subApplicationClass = context.findClass(classDescriptor)!!.mutableClass

            val signatureHookAppClass = fingerprintResult.mutableClass
            signatureHookAppClass.setSuperClass(subApplicationClass.type)

            context.traverseClassHierarchy(subApplicationClass) {
                accessFlags = accessFlags and AccessFlags.FINAL.value.inv()

                transformMethods {
                    val isAttachBaseContextMethod = name == "attachBaseContext"
                        && accessFlags == AccessFlags.PROTECTED or AccessFlags.FINAL
                        && parameterTypes.first() == "Landroid/content/Context;"
                        && returnType.startsWith("V")

                    val methodAccessFlags = if (isAttachBaseContextMethod) {
                        accessFlags and AccessFlags.FINAL.value.inv()
                    } else {
                        accessFlags
                    }

                    ImmutableMethod(
                        definingClass,
                        name,
                        parameters,
                        returnType,
                        methodAccessFlags,
                        annotations,
                        hiddenApiRestrictions,
                        implementation
                    ).toMutable()
                }
            }

            ConstructorFingerprint.result?.let { result ->
                val constructorIndex = result.scanResult.patternScanResult!!.startIndex

                result.mutableMethod.replaceInstruction(
                    index = constructorIndex,
                    smaliInstruction = "invoke-direct {p0}, $classDescriptor-><init>()V"
                )
            }

            val superAttachBaseContextIndex =
                fingerprintResult.scanResult.patternScanResult!!.endIndex

            fingerprintResult.mutableMethod.replaceInstruction(
                index = superAttachBaseContextIndex,
                smaliInstruction = "invoke-super {p0, p1}, $classDescriptor->" +
                    "attachBaseContext(Landroid/content/Context;)V"
            )
        }
    }
}
