package dev.jkcarino.revanced.patches.all.detection.signature.pms

import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.patch.bytecodePatch
import app.revanced.patcher.patch.stringOption

@Suppress("unused")
val bypassSignatureChecksPatch = bytecodePatch(
    name = "Bypass signature verification checks",
    description = "Bypasses the signature verification checks when the app starts up. " +
        "It is recommended to use the unmodified app to work properly.",
    use = false,
) {
    extendWith("extensions/all/detection/signature/pms.rve")

    dependsOn(
        packageNamePatch,
        encodeCertificatePatch
    )

    val packageNameOption =
        stringOption(
            key = "packageName",
            default = "Default",
            values = mapOf("Default" to "Default"),
            title = "Package name",
            description = "The package name of the app, if modified. This must be the same as the " +
                "package name defined in the AndroidManifest.xml.",
            required = true,
        ) { packageName ->
            val packageNamePattern = """^[a-z]\w*(\.[a-z]\w*)+$""".toRegex()
            packageName == "Default" || packageName!!.matches(packageNamePattern)
        }

    val signatureOption =
        stringOption(
            key = "signature",
            default = "Default",
            values = mapOf("Default" to "Default"),
            title = "Base64-encoded signature",
            description = "The base64-encoded signature from the original, unmodified APK. " +
                "This extracts the certificate/signature in the APK by default.",
            required = true,
        ) { signature ->
            signature == "Default" || !signature.isNullOrEmpty()
        }

    execute {
        staticConstructorFingerprint.method.apply {
            val packageNameIndex = staticConstructorFingerprint.patternMatch!!.startIndex
            val customPackageName = packageNameOption.value!!
            val packageName =
                if (customPackageName == packageNameOption.default) {
                    appPackageName
                } else {
                    customPackageName
                }

            replaceInstruction(
                index = packageNameIndex,
                smaliInstruction = """
                    const-string v0, "$packageName"
                """
            )

            val signatureIndex = staticConstructorFingerprint.patternMatch!!.endIndex
            val customSignature = signatureOption.value!!
            val signature =
                if (customSignature == signatureOption.default) {
                    signature
                } else {
                    customSignature.trim()
                }

            replaceInstruction(
                index = signatureIndex,
                smaliInstruction = """
                    const-string v1, "$signature"
                """
            )
        }

        val signatureHookAppClass = staticConstructorFingerprint.originalClassDef

        classes
            .filter { classDef ->
                classDef != signatureHookAppClass
                    && classDef.superclass == "Landroid/app/Application;"
            }
            .forEach { classDef ->
                proxy(classDef)
                    .mutableClass
                    .setSuperClass(signatureHookAppClass.type)
            }
    }
}
