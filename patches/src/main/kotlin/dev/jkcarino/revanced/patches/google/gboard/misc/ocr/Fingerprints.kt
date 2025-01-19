package dev.jkcarino.revanced.patches.google.gboard.misc.ocr

import app.revanced.patcher.fingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal val ocrAccessPointFingerprint = fingerprint {
    accessFlags(AccessFlags.STATIC, AccessFlags.CONSTRUCTOR)
    returns("V")
    parameters()
    opcodes(
        Opcode.CONST_STRING,
        Opcode.CONST_4,
    )
    // Sources:
    //   - Nail Sadykov (X/Twitter: @Nail_Sadykov)
    //   - GMS Flags: https://github.com/polodarb/GMS-Flags
    strings("enable_ocr")
}
