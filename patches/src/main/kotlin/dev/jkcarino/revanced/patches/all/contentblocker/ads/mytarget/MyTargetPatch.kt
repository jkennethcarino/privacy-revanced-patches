package dev.jkcarino.revanced.patches.all.contentblocker.ads.mytarget

import app.revanced.patcher.patch.BytecodePatchContext
import app.revanced.patcher.patch.booleanOption
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import dev.jkcarino.revanced.util.returnEarly
import dev.jkcarino.revanced.util.transformMethods

internal val disableMyTargetOption by lazy {
    booleanOption(
        key = "disableMyTarget",
        default = true,
        title = "myTarget",
        description = "Disable Banner, Interstitial, Rewarded, Native, Native Banner, In-Stream Video and Audio, " +
            "and Carousel ad formats."
    )
}

internal fun BytecodePatchContext.applyMyTargetPatch() {
    val adLoaderBlockMethods = setOf(
        "handleResult",
        "load",
        "loadFromBid",
        "show",
    )

    adLoaderFingerprints.forEach { fingerprint ->
        transformMethods(
            definingClass = fingerprint.originalClassDef.type,
            predicate = { _, method -> method.name in adLoaderBlockMethods },
            transform = MutableMethod::returnEarly
        )
    }

    val promoCardRecyclerViewClassDef =
        promoCardRecyclerViewSetAdapterFingerprint.originalClassDef

    val promoCardRecyclerViewBlockMethods = setOf(
        "renderCard",
        "setAdapter",
        "setPromoCardAdapter"
    )

    transformMethods(
        definingClass = promoCardRecyclerViewClassDef.type,
        predicate = { _, method ->
            method.name in promoCardRecyclerViewBlockMethods
        },
        transform = MutableMethod::returnEarly
    )

    onAdLoadExecutorFingerprint.method.returnEarly()
    myTargetManagerInitSdkFingerprint.method.returnEarly()
}
