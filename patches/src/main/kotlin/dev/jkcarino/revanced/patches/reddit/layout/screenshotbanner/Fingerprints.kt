package dev.jkcarino.revanced.patches.reddit.layout.screenshotbanner

import app.revanced.patcher.fingerprint
import com.android.tools.smali.dexlib2.Opcode

internal val onScreenCapturedFingerprint = fingerprint {
    returns("V")
    parameters()
    custom { method, classDef ->
        method.name == "onScreenCaptured"
            && classDef.interfaces.any { it == "Landroid/app/Activity\$ScreenCaptureCallback;" }
    }
}

internal val screenshotBannerInvokeSuspendFingerprint = fingerprint {
    returns("Ljava/lang/Object;")
    parameters("Ljava/lang/Object;")
    opcodes(
        Opcode.IGET_OBJECT,
        Opcode.SGET_OBJECT,
        Opcode.INVOKE_INTERFACE,
    )
    custom { method, classDef ->
        method.name == "invokeSuspend"
            && classDef.type.contains("/RedditScreenshotTriggerSharingListener\$ScreenshotBanner")
    }
}

internal val screenshotTakenBannerInvokeSuspendFingerprint = fingerprint {
    returns("Ljava/lang/Object;")
    parameters("Ljava/lang/Object;")
    opcodes(
        Opcode.IGET_OBJECT,
        Opcode.SGET,
        Opcode.SGET_OBJECT,
        Opcode.INVOKE_INTERFACE,
    )
    custom { method, classDef ->
        method.name == "invokeSuspend"
            && classDef.type.contains("/ScreenshotTakenBannerKt\$ScreenshotTakenBanner")
    }
}
