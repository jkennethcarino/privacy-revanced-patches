package dev.jkcarino.revanced.patches.all.contentblocker.ads.mytarget

import app.revanced.patcher.patch.BytecodePatchContext
import app.revanced.patcher.patch.booleanOption
import dev.jkcarino.revanced.util.filterMethods
import dev.jkcarino.revanced.util.findMutableMethodOf
import dev.jkcarino.revanced.util.returnEarly

internal val disableMyTargetOption by lazy {
    booleanOption(
        key = "disableMyTarget",
        default = true,
        title = "myTarget",
        description = "Disable Banner, Interstitial, Rewarded, Native, Native Banner, In-Stream Video and Audio, " +
            "and Carousel ad formats."
    )
}

internal fun BytecodePatchContext.applyMyTargetPatch() = buildList {
    val adLoaderBlockMethods = setOf(
        "handleResult",
        "load",
        "loadFromBid",
        "show",
    )
    adLoaderFingerprints.forEach { fingerprint ->
        runCatching {
            val mutableClass = fingerprint.classDef

            mutableClass
                .filterMethods { _, method -> method.name in adLoaderBlockMethods }
                .forEach { method ->
                    mutableClass
                        .findMutableMethodOf(method)
                        .returnEarly()
                }
        }.also(::add)
    }

    val promoCardRecyclerViewBlockMethods = setOf(
        "renderCard",
        "setAdapter",
        "setPromoCardAdapter",
    )
    runCatching {
        val mutableClass = promoCardRecyclerViewSetAdapterFingerprint.classDef

        mutableClass
            .filterMethods { _, method -> method.name in promoCardRecyclerViewBlockMethods }
            .forEach { method ->
                mutableClass
                    .findMutableMethodOf(method)
                    .returnEarly()
            }
    }.also(::add)

    listOf(
        onAdLoadExecutorFingerprint,
        myTargetManagerInitSdkFingerprint
    ).forEach { fingerprint ->
        runCatching {
            fingerprint.method.returnEarly()
        }.also(::add)
    }
}
