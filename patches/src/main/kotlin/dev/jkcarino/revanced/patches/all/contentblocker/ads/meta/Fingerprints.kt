package dev.jkcarino.revanced.patches.all.contentblocker.ads.meta

import app.revanced.patcher.fingerprint
import com.android.tools.smali.dexlib2.AccessFlags

internal val initializeFingerprint = fingerprint {
    accessFlags(AccessFlags.PUBLIC, AccessFlags.STATIC)
    returns("V")
    parameters("Landroid/content/Context;", "L", "L", "Z")
    custom { method, _ ->
        method.definingClass == "Lcom/facebook/ads/internal/dynamicloading/DynamicLoaderFactory;"
            && method.name == "initialize"
    }
}
