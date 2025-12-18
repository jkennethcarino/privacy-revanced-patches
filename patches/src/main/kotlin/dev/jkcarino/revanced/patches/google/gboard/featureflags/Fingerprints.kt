package dev.jkcarino.revanced.patches.google.gboard.featureflags

import app.revanced.patcher.fingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal val featureFlagFingerprint = { flag: String ->
    fingerprint {
        accessFlags(AccessFlags.STATIC, AccessFlags.CONSTRUCTOR)
        returns("V")
        parameters()
        opcodes(
            Opcode.CONST_STRING,
            Opcode.CONST_4,
        )
        strings(flag)
    }
}
