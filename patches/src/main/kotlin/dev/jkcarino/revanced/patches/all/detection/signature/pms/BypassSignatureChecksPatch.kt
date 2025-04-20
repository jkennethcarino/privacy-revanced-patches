package dev.jkcarino.revanced.patches.all.detection.signature.pms

import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.bytecodePatch
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod.Companion.toMutable
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.immutable.ImmutableMethod
import dev.jkcarino.revanced.util.transformMethods
import dev.jkcarino.revanced.util.traverseClassHierarchy

@Suppress("unused")
val bypassSignatureChecksPatch = bytecodePatch(
    name = "Bypass signature verification checks",
    description = "Bypasses the signature verification checks when the app starts up. " +
        "This requires the original, unmodified app to work properly.",
    use = false,
) {
    extendWith("extensions/all/detection/signature/pms.rve")

    dependsOn(
        encodeCertificatePatch,
        replaceSubApplicationPatch,
    )

    execute {
        attachBaseContextFingerprint.method.apply {
            val signatureIndex = attachBaseContextFingerprint.patternMatch!!.startIndex

            replaceInstruction(
                index = signatureIndex,
                smaliInstruction = "const-string v1, \"$signature\""
            )
        }

        if (originalSubApplicationClass.isNotEmpty()) {
            val className = originalSubApplicationClass.replace(".", "/")
            val classDescriptor = "L$className;"
            val subApplicationClass =
                classBy { classDef -> classDef.type == classDescriptor }
                    ?.mutableClass
                    ?: throw PatchException("Could not find the extension.")

            val signatureHookAppClass = attachBaseContextFingerprint.classDef
            signatureHookAppClass.setSuperClass(subApplicationClass.type)

            traverseClassHierarchy(subApplicationClass) {
                accessFlags = accessFlags and AccessFlags.FINAL.value.inv()

                transformMethods {
                    val isAttachBaseContextMethod = name == "attachBaseContext"
                        && accessFlags == AccessFlags.PROTECTED.value or AccessFlags.FINAL.value
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

            constructorFingerprint.method.apply {
                val constructorIndex = constructorFingerprint.patternMatch!!.startIndex

                replaceInstruction(
                    index = constructorIndex,
                    smaliInstruction = "invoke-direct {p0}, $classDescriptor-><init>()V"
                )
            }

            attachBaseContextFingerprint.method.apply {
                val superAttachBaseContextIndex = attachBaseContextFingerprint.patternMatch!!.endIndex

                replaceInstruction(
                    index = superAttachBaseContextIndex,
                    smaliInstruction = "invoke-super {p0, p1}, $classDescriptor->" +
                        "attachBaseContext(Landroid/content/Context;)V"
                )
            }
        }
    }
}
