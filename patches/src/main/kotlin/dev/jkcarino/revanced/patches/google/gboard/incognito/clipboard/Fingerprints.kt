package dev.jkcarino.revanced.patches.google.gboard.incognito.clipboard

import app.revanced.patcher.fingerprint
import com.android.tools.smali.dexlib2.Opcode

internal val onPrimaryClipChangedFingerprint = fingerprint {
    returns("V")
    parameters()
    opcodes(
        Opcode.INVOKE_STATIC,
        Opcode.MOVE_RESULT,
        Opcode.IF_EQZ,
        Opcode.RETURN_VOID,
    )
    strings("clipboard_primary_uri", "")
    custom { method, _ ->
        method.name == "onPrimaryClipChanged"
    }
}
