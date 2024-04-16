package dev.jkcarino.revanced.patches.google.gboard.misc.incognito.fingerprints

import app.revanced.patcher.extensions.or
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import dev.jkcarino.revanced.util.patch.LiteralValueFingerprint

internal object IsIncognitoModeFingerprint : LiteralValueFingerprint(
    returnType = "Z",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.STATIC,
    parameters = listOf("Landroid/view/inputmethod/EditorInfo;"),
    opcodes = listOf(
        Opcode.IF_EQZ,
        Opcode.IGET,
        Opcode.CONST_HIGH16,
        Opcode.AND_INT_2ADDR,
        Opcode.IF_EQZ,
        Opcode.CONST_4,
        Opcode.RETURN,
        Opcode.CONST_4,
        Opcode.RETURN,
    ),
    literalSupplier = { 0x1000000 } // EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING
)
