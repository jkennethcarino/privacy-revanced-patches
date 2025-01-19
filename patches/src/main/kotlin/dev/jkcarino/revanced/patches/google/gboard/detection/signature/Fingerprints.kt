package dev.jkcarino.revanced.patches.google.gboard.detection.signature

import app.revanced.patcher.fingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal val checkSignatureFingerprint = fingerprint {
    accessFlags(AccessFlags.PUBLIC, AccessFlags.STATIC)
    returns("Z")
    parameters("Landroid/content/Context;", "Ljava/lang/String;")
    opcodes(
        Opcode.CONST_4,
        Opcode.NEW_ARRAY,
        // 1st byte array
        Opcode.SGET_OBJECT,
        Opcode.CONST_4,
        Opcode.APUT_OBJECT,
        // 2nd byte array
        Opcode.SGET_OBJECT,
        Opcode.CONST_4,
        Opcode.APUT_OBJECT,
        // 3rd byte array
        Opcode.SGET_OBJECT,
        Opcode.CONST_4,
        Opcode.APUT_OBJECT,
        // Invokes a static method to calculate the
        // SHA-256 digest of the app package's signature
        Opcode.INVOKE_STATIC,
        Opcode.MOVE_RESULT_OBJECT,
    )
}
