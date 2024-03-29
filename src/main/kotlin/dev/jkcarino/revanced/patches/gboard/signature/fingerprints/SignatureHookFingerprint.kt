package dev.jkcarino.revanced.patches.gboard.signature.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags

internal object SignatureHookAppFingerprint : MethodFingerprint(
    accessFlags = AccessFlags.PUBLIC or AccessFlags.CONSTRUCTOR,
    parameters = listOf(),
    customFingerprint = { methodDef, _ ->
        methodDef.definingClass == "Ldev/jkcarino/revanced/integrations/gboard/SignatureHookApp;"
    }
)
