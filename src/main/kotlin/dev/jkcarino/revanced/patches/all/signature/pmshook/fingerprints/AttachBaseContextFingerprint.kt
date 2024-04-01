package dev.jkcarino.revanced.patches.all.signature.pmshook.fingerprints

import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal object AttachBaseContextFingerprint : MethodFingerprint(
    returnType = "V",
    accessFlags = AccessFlags.PUBLIC.value,
    parameters = listOf("Landroid/content/Context;"),
    opcodes = listOf(
        Opcode.CONST_STRING,
        Opcode.INVOKE_DIRECT,
        Opcode.INVOKE_VIRTUAL,
        Opcode.INVOKE_SUPER,
    ),
    customFingerprint = { methodDef, classDef ->
        classDef.type.endsWith("SignatureHookApp;") && methodDef.name == "attachBaseContext"
    }
)
