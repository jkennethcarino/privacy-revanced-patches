package dev.jkcarino.revanced.patches.all.contentblocker.ads.bigo

import app.revanced.patcher.fingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal val bigoAdSdkInitializeFingerprint = fingerprint {
    accessFlags(AccessFlags.PUBLIC, AccessFlags.STATIC)
    returns("V")
    strings(
        "Bigo Ads SDK init had been invoked.",
        "Bigo Ads SDK wait to initing due to empty config.",
        "Avoid initializing Bigo Ads SDK repeatedly.",
    )
}

internal val abstractAdLoaderLoadAdFingerprint = fingerprint {
    accessFlags(AccessFlags.PUBLIC)
    returns("V")
    opcodes(
        Opcode.INVOKE_DIRECT,
        Opcode.MOVE_RESULT,
        Opcode.IF_NEZ,
        Opcode.INVOKE_DIRECT,
    )
    custom { method, _ ->
        method.definingClass == "Lsg/bigo/ads/controller/loader/AbstractAdLoader;"
            && method.name == "loadAd"
    }
}

internal val splashAdFingerprint = fingerprint {
    strings(
        "splash_duration",
        "splash_close",
    )
    custom { method, _ ->
        method.definingClass.startsWith("Lsg/bigo/ads/ad/splash/")
    }
}
