package dev.jkcarino.revanced.patches.all.detection.signature.pms

import app.revanced.patcher.fingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal val attachBaseContextFingerprint = fingerprint {
    accessFlags(AccessFlags.PUBLIC)
    returns("V")
    parameters("Landroid/content/Context;")
    opcodes(
        Opcode.CONST_STRING,
        Opcode.INVOKE_DIRECT,
        Opcode.INVOKE_VIRTUAL,
        Opcode.INVOKE_SUPER,
    )
    custom { method, classDef ->
        classDef.type.endsWith("/SignatureHookApp;") && method.name == "attachBaseContext"
    }
}

internal val constructorFingerprint = fingerprint {
    accessFlags(AccessFlags.PUBLIC, AccessFlags.CONSTRUCTOR)
    returns("V")
    parameters()
    opcodes(Opcode.INVOKE_DIRECT)
    custom { method, _ ->
        method.definingClass.endsWith("/SignatureHookApp;")
    }
}
