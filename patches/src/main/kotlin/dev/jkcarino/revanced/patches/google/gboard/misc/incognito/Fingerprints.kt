package dev.jkcarino.revanced.patches.google.gboard.misc.incognito

import app.revanced.patcher.fingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import dev.jkcarino.revanced.util.literal

internal val isIncognitoModeFingerprint = fingerprint {
    accessFlags(AccessFlags.PUBLIC, AccessFlags.STATIC)
    returns("Z")
    parameters("Landroid/view/inputmethod/EditorInfo;")
    opcodes(
        Opcode.IF_EQZ,
        Opcode.IGET,
        Opcode.CONST_HIGH16,
        Opcode.AND_INT_2ADDR,
        Opcode.IF_EQZ,
        Opcode.CONST_4,
        Opcode.RETURN,
        Opcode.CONST_4,
        Opcode.RETURN,
    )
    literal { 0x1000000 } // EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING
}

/**
 * Introduced in Gboard 14.4.06.646482735 beta.
 */
internal val isIncognitoModeInlinedFingerprint = fingerprint {
    accessFlags(AccessFlags.PUBLIC, AccessFlags.FINAL)
    returns("V")
    parameters("Landroid/view/inputmethod/EditorInfo;", "Z")
    opcodes(
        Opcode.IF_EQZ,
        Opcode.IGET,
        Opcode.CONST_HIGH16,
        Opcode.AND_INT_2ADDR,
        Opcode.IF_EQZ,
        Opcode.MOVE,
        Opcode.GOTO,
        Opcode.CONST_4,
        Opcode.INVOKE_VIRTUAL,
    )
    custom { method, _ ->
        method.name == "onStartInput"
    }
}
