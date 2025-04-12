package dev.jkcarino.revanced.patches.all.contentblocker.ads.admob

import app.revanced.patcher.extensions.InstructionExtensions.instructionsOrNull
import app.revanced.patcher.patch.BytecodePatchContext
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.booleanOption
import com.android.tools.smali.dexlib2.iface.reference.StringReference
import dev.jkcarino.revanced.util.filterMethods
import dev.jkcarino.revanced.util.findMutableMethodOf
import dev.jkcarino.revanced.util.getReference
import dev.jkcarino.revanced.util.proxy
import dev.jkcarino.revanced.util.returnEarly

internal val disableGoogleAdMobOption by lazy {
    booleanOption(
        key = "disableGoogleAdMob",
        default = true,
        title = "Google AdMob",
        description = "Disable Banner, Interstitial, Native, Rewarded, Rewarded Interstitial, and App Open ad formats."
    )
}

internal fun BytecodePatchContext.applyGoogleAdMobPatch() = buildList {
    runCatching {
        // Common strings present in AppOpenAd, InterstitialAd, RewardedAd,
        // and RewardedInterstitialAd's load method
        val preconditions = setOf(
            "Context cannot be null.",
            "AdUnitId cannot be null.",
            "#008 Must be called on the main UI thread.",
        )
        classes
            .filterMethods { _, method ->
                if (method.returnType != "V" ||
                    method.parameters.isEmpty() ||
                    method.parameters.first().type != "Landroid/content/Context;"
                ) {
                    return@filterMethods false
                }

                val instructions = method.instructionsOrNull
                    ?: return@filterMethods false

                val strings = preconditions.toMutableList()

                instructions.forEach instructions@{ instruction ->
                    val string = instruction.getReference<StringReference>()
                        ?.string
                        ?: return@instructions

                    val index = strings.indexOfFirst {
                        string.contains(
                            other = it,
                            // The AppOpenAd's load method has "adUnitId" instead of "AdUnitId"
                            ignoreCase = true
                        )
                    }
                    if (index == -1) return@instructions

                    // Found a match
                    strings.removeAt(index)
                }

                return@filterMethods strings.isEmpty()
            }
            .ifEmpty { throw PatchException("No load ad method found") }
            .forEach { method ->
                proxy(method.definingClass)
                    .mutableClass
                    .findMutableMethodOf(method)
                    .returnEarly()
            }
    }.also(::add)

    runCatching {
        val blockMethods = setOf(
            "requestBannerAd",
            "requestInterstitialAd",
            "requestNativeAd",
            "showInterstitial",
        )
        val mutableClass =
            proxy("Lcom/google/ads/mediation/AbstractAdViewAdapter;")
                .mutableClass

        mutableClass
            .filterMethods { _, method -> method.name in blockMethods }
            .forEach { method ->
                mutableClass
                    .findMutableMethodOf(method)
                    .returnEarly()
            }
    }.also(::add)

    listOf(
        googleAdMobBaseAdViewFingerprint,
        googleAdMobBannerAdFingerprint,
        googleAdMobNativeAdFingerprint,
    ).forEach { fingerprint ->
        runCatching {
            fingerprint.method.returnEarly()
        }.also(::add)
    }
}
