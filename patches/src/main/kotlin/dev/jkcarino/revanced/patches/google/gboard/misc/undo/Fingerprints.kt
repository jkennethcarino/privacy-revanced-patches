package dev.jkcarino.revanced.patches.google.gboard.misc.undo

import app.revanced.patcher.fingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal val undoAccessPointFingerprint = fingerprint {
    accessFlags(AccessFlags.STATIC, AccessFlags.CONSTRUCTOR)
    returns("V")
    parameters()
    opcodes(
        Opcode.CONST_STRING,
        Opcode.CONST_4,
    )
    // Sources:
    //   - Rboard Theme Manager
    //   - GSM Flags: https://github.com/polodarb/GMS-Flags
    strings("undo_access_point")
}
