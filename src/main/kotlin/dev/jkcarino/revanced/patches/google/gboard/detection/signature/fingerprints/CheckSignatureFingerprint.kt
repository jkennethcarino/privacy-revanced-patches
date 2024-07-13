package dev.jkcarino.revanced.patches.google.gboard.detection.signature.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal object CheckSignatureFingerprint : MethodFingerprint(
    returnType = "Z",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.STATIC,
    parameters = listOf("Landroid/content/Context;", "Ljava/lang/String;"),
    opcodes = listOf(
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
)
