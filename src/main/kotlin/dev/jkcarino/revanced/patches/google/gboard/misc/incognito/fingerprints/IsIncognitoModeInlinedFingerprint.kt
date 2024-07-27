package dev.jkcarino.revanced.patches.google.gboard.misc.incognito.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal object IsIncognitoModeInlinedFingerprint : MethodFingerprint(
    returnType = "V",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    parameters = listOf("Landroid/view/inputmethod/EditorInfo;", "Z"),
    opcodes = listOf(
        Opcode.IF_EQZ,
        Opcode.IGET,
        Opcode.CONST_HIGH16,
        Opcode.AND_INT_2ADDR,
        Opcode.IF_EQZ,
        Opcode.MOVE,
        Opcode.GOTO,
        Opcode.CONST_4,
        Opcode.INVOKE_VIRTUAL,
    ),
    customFingerprint = { methodDef, _ ->
        methodDef.name == "onStartInput"
    }
)
