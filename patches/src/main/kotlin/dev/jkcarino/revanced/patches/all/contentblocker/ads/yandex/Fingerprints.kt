package dev.jkcarino.revanced.patches.all.contentblocker.ads.yandex

import app.revanced.patcher.fingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal val loadFeedFingerprint = fingerprint {
    accessFlags(AccessFlags.PUBLIC, AccessFlags.FINAL)
    returns("V")
    parameters("L")
    opcodes(
        Opcode.CONST_STRING,
        Opcode.INVOKE_STATIC,
        Opcode.IGET_OBJECT,
        Opcode.IGET_OBJECT,
    )
    strings(
        "feedItem",
        "getContext(...)",
    )
}
