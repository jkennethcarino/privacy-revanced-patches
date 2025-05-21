package dev.jkcarino.revanced.patches.all.detection.signature.pms

import app.revanced.patcher.fingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal val staticConstructorFingerprint = fingerprint {
    accessFlags(AccessFlags.STATIC, AccessFlags.CONSTRUCTOR)
    parameters()
    opcodes(Opcode.CONST_STRING, Opcode.CONST_STRING)
    strings("<package-name>", "<signature>")
    custom { _, classDef ->
        classDef.type.endsWith("/SignatureHookApp;")
    }
}
