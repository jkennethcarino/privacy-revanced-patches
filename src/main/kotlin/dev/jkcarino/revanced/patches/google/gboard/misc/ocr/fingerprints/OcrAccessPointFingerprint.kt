package dev.jkcarino.revanced.patches.google.gboard.misc.ocr.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal object OcrAccessPointFingerprint : MethodFingerprint(
    returnType = "V",
    accessFlags = AccessFlags.STATIC or AccessFlags.CONSTRUCTOR,
    parameters = listOf(),
    opcodes = listOf(
        Opcode.CONST_STRING,
        Opcode.CONST_4,
    ),
    // Sources:
    //   - Nail Sadykov (X/Twitter: @Nail_Sadykov)
    //   - GMS Flags: https://github.com/polodarb/GMS-Flags
    strings = listOf("enable_ocr")
)
