package dev.jkcarino.revanced.patches.all.contentblocker.ads.admob

import app.revanced.patcher.extensions.InstructionExtensions.instructionsOrNull
import app.revanced.patcher.patch.BytecodePatchContext
import app.revanced.patcher.patch.booleanOption
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import com.android.tools.smali.dexlib2.iface.reference.StringReference
import dev.jkcarino.revanced.util.getReference
import dev.jkcarino.revanced.util.returnEarly
import dev.jkcarino.revanced.util.transformMethods

internal val disableGoogleAdMobOption by lazy {
    booleanOption(
        key = "disableGoogleAdMob",
        default = true,
        title = "Google AdMob",
        description = "Disable Banner, Interstitial, Native, Rewarded, Rewarded Interstitial, and App Open ad formats."
    )
}

internal fun BytecodePatchContext.applyGoogleAdMobPatch() {
    // Common strings present in AppOpenAd, InterstitialAd, RewardedAd,
    // and RewardedInterstitialAd's load method
    val preconditions = setOf(
        "Context cannot be null.",
        "AdUnitId cannot be null.",
        "#008 Must be called on the main UI thread.",
    )
    transformMethods(
        predicate = predicate@{ _, method ->
            if (method.returnType != "V" ||
                method.parameters.isEmpty() ||
                method.parameters.first().type != "Landroid/content/Context;"
            ) {
                return@predicate false
            }

            val instructions = method.instructionsOrNull ?: return@predicate false

            val strings = preconditions.toMutableList()

            instructions.forEach instructions@{ instruction ->
                val string = instruction.getReference<StringReference>()?.string ?: return@instructions
                val index = strings.indexOfFirst {
                    string.contains(
                        other = it,
                        // The AppOpenAd's load method has "adUnit" instead of "AdUnitId"
                        ignoreCase = true
                    )
                }
                if (index == -1) return@instructions

                // Found a match
                strings.removeAt(index)
            }

            return@predicate strings.isEmpty()
        },
        transform = MutableMethod::returnEarly
    )

    val abstractAdViewAdapterMethods = setOf(
        "requestBannerAd",
        "requestInterstitialAd",
        "requestNativeAd",
        "showInterstitial",
    )
    transformMethods(
        definingClass = "Lcom/google/ads/mediation/AbstractAdViewAdapter;",
        predicate = { _, method -> method.name in abstractAdViewAdapterMethods },
        transform = MutableMethod::returnEarly
    )

    listOf(
        googleAdMobBaseAdViewFingerprint,
        googleAdMobBannerAdFingerprint,
        googleAdMobNativeAdFingerprint,
    ).forEach { fingerprint ->
        fingerprint.method.returnEarly()
    }
}
