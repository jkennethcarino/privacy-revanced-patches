package dev.jkcarino.revanced.patches.reddit.misc.sharing.url

import app.revanced.patcher.fingerprint
import com.android.tools.smali.dexlib2.Opcode

internal val createShareLinkFingerprint = fingerprint {
    returns("Ljava/lang/String;")
    parameters(
        "Ljava/lang/String;",
        "Ljava/util/Map;",
    )
    strings(
        "url",
        "getQueryParameterNames(...)",
        "toString(...)",
    )
}

internal val generateShareLinkFingerprint = fingerprint {
    returns("L")
    opcodes(
        Opcode.MOVE_OBJECT,
        Opcode.IGET_OBJECT,
        null,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT,
    )
    strings(
        "shareTrigger",
        "shareAction",
        "permalink",
        "share_id",
    )
}
