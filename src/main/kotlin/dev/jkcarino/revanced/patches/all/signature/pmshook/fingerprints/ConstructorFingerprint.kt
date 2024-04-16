package dev.jkcarino.revanced.patches.all.signature.pmshook.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal object ConstructorFingerprint : MethodFingerprint(
    returnType = "V",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.CONSTRUCTOR,
    parameters = listOf(),
    opcodes = listOf(Opcode.INVOKE_DIRECT),
    customFingerprint = { methodDef, _ ->
        methodDef.definingClass.endsWith("/SignatureHookApp;")
    }
)
