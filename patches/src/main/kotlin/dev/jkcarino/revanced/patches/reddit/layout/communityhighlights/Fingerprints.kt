package dev.jkcarino.revanced.patches.reddit.layout.communityhighlights

import app.revanced.patcher.fingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal val subredditInfoByIdToStringFingerprint = fingerprint {
    returns("Ljava/lang/String;")
    parameters()
    strings(
        "SubredditInfoById(__typename=",
        ", highlightedPostsModeratorsInfoFragment=",
    )
}

internal val invokeFingerprint = fingerprint {
    strings(
        "\$this\$AnimatedContent",
        "collapse_expand_highlight",
    )
    custom { method, _ ->
        method.name == "invoke"
    }
}

internal val loadedToStringFingerprint = fingerprint {
    returns("Ljava/lang/String;")
    parameters()
    strings("Loaded(highlightedItems=")
}

internal val unitToStringFingerprint = fingerprint {
    returns("Ljava/lang/String;")
    parameters()
    strings("kotlin.Unit")
    custom { method, _ ->
        method.name == "toString"
    }
}

internal val unitFingerprint = fingerprint {
    accessFlags(AccessFlags.STATIC, AccessFlags.CONSTRUCTOR)
    parameters()
    opcodes(
        Opcode.NEW_INSTANCE,
        Opcode.INVOKE_DIRECT,
        Opcode.SPUT_OBJECT,
    )
}
