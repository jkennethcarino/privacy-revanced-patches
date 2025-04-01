package dev.jkcarino.revanced.patches.all.contentblocker.ads

import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.bytecodePatch
import dev.jkcarino.revanced.patches.all.contentblocker.ads.admob.applyGoogleAdMobPatch
import dev.jkcarino.revanced.patches.all.contentblocker.ads.admob.disableGoogleAdMobOption
import dev.jkcarino.revanced.patches.all.contentblocker.ads.applovin.applyAppLovinMaxPatch
import dev.jkcarino.revanced.patches.all.contentblocker.ads.applovin.disableAppLovinMaxOption
import dev.jkcarino.revanced.patches.all.contentblocker.ads.bigo.applyBigoPatch
import dev.jkcarino.revanced.patches.all.contentblocker.ads.bigo.disableBigoOption
import dev.jkcarino.revanced.patches.all.contentblocker.ads.meta.applyMetaAudienceNetworkPatch
import dev.jkcarino.revanced.patches.all.contentblocker.ads.meta.disableMetaAudienceNetworkOption
import dev.jkcarino.revanced.patches.all.contentblocker.ads.mintegral.applyMintegralPatch
import dev.jkcarino.revanced.patches.all.contentblocker.ads.mintegral.disableMintegralOption
import dev.jkcarino.revanced.patches.all.contentblocker.ads.mytarget.applyMyTargetPatch
import dev.jkcarino.revanced.patches.all.contentblocker.ads.mytarget.disableMyTargetOption
import dev.jkcarino.revanced.patches.all.contentblocker.ads.pangle.applyPanglePatch
import dev.jkcarino.revanced.patches.all.contentblocker.ads.pangle.disablePangleOption
import dev.jkcarino.revanced.patches.all.contentblocker.ads.topon.applyTopOnPatch
import dev.jkcarino.revanced.patches.all.contentblocker.ads.topon.disableTopOnOption
import dev.jkcarino.revanced.patches.all.contentblocker.ads.unity.applyUnityPatch
import dev.jkcarino.revanced.patches.all.contentblocker.ads.unity.disableUnityOption
import dev.jkcarino.revanced.patches.all.contentblocker.ads.vungle.applyVunglePatch
import dev.jkcarino.revanced.patches.all.contentblocker.ads.vungle.disableVungleOption
import dev.jkcarino.revanced.patches.all.contentblocker.ads.yandex.applyYandexPatch
import dev.jkcarino.revanced.patches.all.contentblocker.ads.yandex.disableYandexOption
import java.util.logging.Logger

@Suppress("unused")
val disableAdsPatch = bytecodePatch(
    name = "Disable mobile ads",
    description = "Disables banner, interstitial, and other ad formats."
) {
    val logger = Logger.getLogger(this::class.java.name)

    execute {
        val options = mapOf(
            disableAppLovinMaxOption to ::applyAppLovinMaxPatch,
            disableBigoOption to ::applyBigoPatch,
            disableGoogleAdMobOption to ::applyGoogleAdMobPatch,
            disableMetaAudienceNetworkOption to ::applyMetaAudienceNetworkPatch,
            disableMintegralOption to ::applyMintegralPatch,
            disableMyTargetOption to ::applyMyTargetPatch,
            disablePangleOption to ::applyPanglePatch,
            disableTopOnOption to ::applyTopOnPatch,
            disableUnityOption to ::applyUnityPatch,
            disableVungleOption to ::applyVunglePatch,
            disableYandexOption to ::applyYandexPatch,
        )

        options.forEach { (option, patch) ->
            val isEnabled by option
            if (!isEnabled!!) {
                return@forEach
            }

            val message = try {
                patch()

                "[Found] ${option.title} disabled."
            } catch (_: PatchException) {
                "[Skipped] ${option.title} was not found."
            }

            logger.info(message)
        }
    }
}
