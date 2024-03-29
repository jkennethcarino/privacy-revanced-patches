package dev.jkcarino.revanced.patches.gboard.signature

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.or
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.CompatiblePackage
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod.Companion.toMutable
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.immutable.ImmutableMethod
import dev.jkcarino.revanced.patches.gboard.signature.fingerprints.SignatureHookAppFingerprint
import dev.jkcarino.revanced.util.transformMethods
import dev.jkcarino.revanced.util.traverseClassHierarchy

@Patch(
    name = "Bypass Gboard signature verification checks",
    description = "Bypasses the signature verification checks performed by Gboard on app startup.",
    dependencies = [
        ReplaceImeLatinAppPatch::class,
    ],
    compatiblePackages = [CompatiblePackage("com.google.android.inputmethod.latin")],
    use = false,
    requiresIntegrations = true
)
object BypassSignatureChecksPatch : BytecodePatch(
    setOf(SignatureHookAppFingerprint)
) {
    override fun execute(context: BytecodeContext) {
        val signatureHookAppClass = SignatureHookAppFingerprint.result!!.mutableClass

        context.traverseClassHierarchy(signatureHookAppClass) {
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
    }
}
