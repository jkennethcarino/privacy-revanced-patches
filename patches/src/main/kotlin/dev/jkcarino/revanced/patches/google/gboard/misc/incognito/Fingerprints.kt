package dev.jkcarino.revanced.patches.google.gboard.misc.incognito

import app.revanced.patcher.fingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import dev.jkcarino.revanced.util.literal

private const val IME_FLAG_NO_PERSONALIZED_LEARNING: Long = 0x1000000

private val imeNoPersonalizedLearningOpCodes = listOf(
    Opcode.IF_EQZ,
    Opcode.IGET,
    Opcode.CONST_HIGH16,
    Opcode.AND_INT_2ADDR,
    Opcode.IF_EQZ,
    Opcode.CONST_4,
    Opcode.RETURN,
    Opcode.CONST_4,
    Opcode.RETURN,
).toTypedArray()

internal val isIncognitoModeFingerprint = fingerprint {
    accessFlags(AccessFlags.PUBLIC, AccessFlags.STATIC)
    returns("Z")
    parameters("Landroid/view/inputmethod/EditorInfo;")
    opcodes(*imeNoPersonalizedLearningOpCodes)
    literal { IME_FLAG_NO_PERSONALIZED_LEARNING }
}

/**
 * Introduced in Gboard 14.9.06.696880419 beta.
 */
internal val isIncognitoModeV2Fingerprint = fingerprint {
    accessFlags(AccessFlags.PUBLIC, AccessFlags.FINAL)
    returns("Z")
    parameters()
    opcodes(*imeNoPersonalizedLearningOpCodes)
    literal { IME_FLAG_NO_PERSONALIZED_LEARNING }
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
