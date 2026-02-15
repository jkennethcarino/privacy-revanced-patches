package dev.jkcarino.revanced.patches.reddit.layout.search.ask

import app.revanced.patcher.fingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal val staticConstructorFingerprint = fingerprint {
    accessFlags(AccessFlags.STATIC, AccessFlags.CONSTRUCTOR)
    strings(
        "isSearchBarAskButtonHoldoutEnabled",
        "isSearchBarAskButtonHoldoutEnabled()Z",
    )
}

internal val isSearchBarAskButtonHoldoutEnabledFingerprint = fingerprint {
    returns("Z")
    parameters()
    opcodes(
        Opcode.SGET_OBJECT,
        Opcode.AGET_OBJECT,
        Opcode.IGET_OBJECT,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT,
    )
}
