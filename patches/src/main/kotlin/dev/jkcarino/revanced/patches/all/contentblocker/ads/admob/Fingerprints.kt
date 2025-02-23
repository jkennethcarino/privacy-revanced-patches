package dev.jkcarino.revanced.patches.all.contentblocker.ads.admob

import app.revanced.patcher.fingerprint
import com.android.tools.smali.dexlib2.Opcode

internal val googleAdMobBaseAdViewFingerprint = fingerprint {
    returns("V")
    opcodes(
        Opcode.CONST_STRING,
        Opcode.INVOKE_STATIC,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.INVOKE_STATIC,
    )
    strings("#008 Must be called on the main UI thread.")
    custom { _, classDef ->
        classDef.superclass == "Landroid/view/ViewGroup;"
    }
}

internal val googleAdMobBannerAdFingerprint = fingerprint {
    returns("V")
    strings("The ad size and ad unit ID must be set before loadAd is called.")
}

internal val googleAdMobNativeAdFingerprint = fingerprint {
    returns("V")
    opcodes(
        Opcode.IGET_OBJECT,
        Opcode.INVOKE_STATIC,
        Opcode.SGET_OBJECT,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT,
    )
    strings("Failed to load ad.")
    custom { _, classDef ->
        classDef.superclass == "Ljava/lang/Object;"
    }
}
